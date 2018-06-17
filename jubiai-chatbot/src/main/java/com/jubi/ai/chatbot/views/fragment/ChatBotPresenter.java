package com.jubi.ai.chatbot.views.fragment;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jubi.ai.chatbot.models.BasicResponse;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.models.ChatButton;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.models.OutgoingMessage;
import com.jubi.ai.chatbot.models.RestError;
import com.jubi.ai.chatbot.persistence.ChatMessage;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.viewModel.ChatMessageListViewModel;
import com.jubi.ai.chatbot.views.adapter.ChatMessageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatBotPresenter {

    private ChatBotView chatBotView;
    private ChatBotModel chatBotModel;
    private CompositeSubscription compositeSubscription;
    private ChatMessageListViewModel chatMessageListViewModel;


    public ChatBotPresenter(ChatBotView chatBotView, ChatBotModel chatBotModel) {
        this.chatBotView = chatBotView;
        this.chatBotModel = chatBotModel;
        compositeSubscription = new CompositeSubscription();
    }

    public void sendChat(String chatMessage) {
        chatBotModel.insertChat(chatMessage);
        pushMessage(chatMessage);
    }

    public void sendSelectedOption(ChatOption item) {
        chatBotModel.insertChat(item.getText());
        pushMessage(item.getData());
        startFakeTypingMessageListener();
    }

    public void sendSelectedOption(ChatButton item) {
        chatBotModel.insertChat(item.getText());
        pushMessage(item.getData());
        startFakeTypingMessageListener();
    }

    public void updateChat(ChatMessage chatMessage) {
        chatBotModel.updateChat(chatMessage);
    }

    public void receiveChat(ChatMessage chatMessage) {
        chatBotModel.insertChat(chatMessage);
    }

    public void setChatBotConfig(ChatBotConfig chatBotConfig) {
        chatBotModel.setChatBotConfig(chatBotConfig);
    }

    public void isGCMTokenSaved() {
        if (!chatBotModel.isGCMTokenSaved()) {
            chatBotView.showFCMNotSyncView();
        } else {
            chatBotView.showNoChatMessagesView();
        }
    }

    public void pushMessage(String message) {
        compositeSubscription.add(chatBotModel.pushMessage(message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<BasicResponse>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("PushFCMToken", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("PushFCMToken", "onError " + e.getMessage());
                        chatBotView.onMessagePushFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<BasicResponse> response) {
                        Log.d("PushFCMToken", "onNext " + response.code());
                        if (response.code() == 200) {
                            chatBotView.onMessagePushed();
                        } else {
                            BasicResponse basicResponse = Util.handleError(response.errorBody());
                            chatBotView.onMessagePushFailed(basicResponse.getError());
                        }
                    }
                }));

    }

    public void clear() {
        compositeSubscription.clear();
    }

    public void onSendClick(String message) {
        if (!Util.textIsEmpty(message)) {
            chatBotView.sendChat(message);
        }
    }

    public void bindViewModel(FragmentActivity fragmentActivity, LifecycleOwner owner) {
        chatMessageListViewModel = ViewModelProviders.of(fragmentActivity).get(ChatMessageListViewModel.class);
        chatMessageListViewModel.getChatMessageList().observe(owner, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessages) {
                chatBotView.onChatViewModelUpdate(chatMessages);
            }
        });
    }

    public void onInputMessageChangeListener(EditText messageText, final ImageView send) {
        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableDisableSend(send, Util.textIsEmpty(charSequence.toString()) ? false : true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void enableDisableSend(ImageView send, boolean enable) {
        send.setAlpha(enable ? 1f : 0.3f);
        send.setEnabled(enable);
        send.setClickable(enable);
    }

    public void startFakeIncomingMessageListener(final String messageStr) {
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (messageStr.contains("image")) {
                    receiveChat(chatBotModel.imageChatMessage());
                } else if (messageStr.contains("option")) {
                    receiveChat(chatBotModel.textChatMessageWithOptions());
                } else if (messageStr.contains("text")) {
                    receiveChat(chatBotModel.textChatMessage());
                } else if (messageStr.contains("carousel")) {
                    receiveChat(chatBotModel.textChatMessageWithCarouselOptions());
                } else if (messageStr.contains("video")) {
                    receiveChat(chatBotModel.videoChatMessage());
                }

            }
        }.start();
    }

    public void cameraImageChatMessage(Uri uri) {
        receiveChat(chatBotModel.cameraImageChatMessage(uri));
    }

    public void startFakeTypingMessageListener() {
        new CountDownTimer(500, 100) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                chatBotModel.insertChat();
            }
        }.start();
    }

    public ChatMessage getChatFromMessage(String mess) {
        return chatBotModel.makeChatFromMessage(mess);
    }

}
