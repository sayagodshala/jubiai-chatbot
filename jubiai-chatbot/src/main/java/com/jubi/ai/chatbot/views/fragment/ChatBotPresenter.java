package com.jubi.ai.chatbot.views.fragment;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.jubi.ai.chatbot.models.BasicResponse;
import com.jubi.ai.chatbot.models.ChatButton;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.persistence.ChatMessage;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.viewModel.ChatMessageListViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotPresenter {

    private ChatBotView chatBotView;
    private ChatBotModel chatBotModel;
    private ChatMessageListViewModel chatMessageListViewModel;


    public ChatBotPresenter(ChatBotView chatBotView, ChatBotModel chatBotModel) {
        this.chatBotView = chatBotView;
        this.chatBotModel = chatBotModel;
        this.chatBotModel.deleteTypingMessage();
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

    public void sendChat(ChatMessage chatMessage) {
        chatBotModel.insertChat(chatMessage);
    }

    public void pushMessage(String message) {
        Call<BasicResponse> callBack = chatBotModel.pushMessage(message);
        callBack.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.code() == 200) {
                    chatBotView.onMessagePushed();
                } else {
                    BasicResponse basicResponse = Util.handleError(response.errorBody());
                    chatBotView.onMessagePushFailed(basicResponse.getError());
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.d("PushFCMToken", "onError " + t.getMessage());
                chatBotView.onMessagePushFailed(t.getMessage());
            }
        });
    }

    public void pushImageMessage(String url) {
        Call<BasicResponse> callBack = chatBotModel.pushImageMessage(url);
        callBack.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.code() == 200) {
                    chatBotView.onMessagePushed();
                } else {
                    BasicResponse basicResponse = Util.handleError(response.errorBody());
                    chatBotView.onMessagePushFailed(basicResponse.getError());
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Log.d("PushFCMToken", "onError " + t.getMessage());
                chatBotView.onMessagePushFailed(t.getMessage());
            }
        });


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

    public void imageChatMessage(String url) {
        sendChat(chatBotModel.cameraImageChatMessage(url));
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
}
