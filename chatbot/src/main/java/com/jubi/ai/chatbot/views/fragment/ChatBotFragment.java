package com.jubi.ai.chatbot.views.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.AnswerType;
import com.jubi.ai.chatbot.enums.MaterialColor;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.listeners.ChatBotFragmentListener;
import com.jubi.ai.chatbot.listeners.IResultListener;
import com.jubi.ai.chatbot.models.BotMessage;
import com.jubi.ai.chatbot.models.Chat;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.models.ChatButton;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.models.WebViewData;
import com.jubi.ai.chatbot.persistence.ChatMessage;
import com.jubi.ai.chatbot.util.CustomPopoverView;
import com.jubi.ai.chatbot.util.ItemOffsetDecoration;
import com.jubi.ai.chatbot.util.UiUtils;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.activity.WebViewActivity;
import com.jubi.ai.chatbot.views.adapter.ChatMessageAdapter;
import com.jubi.ai.chatbot.views.adapter.ChatMessageOptionAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.xw.repo.XEditText;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;


public class ChatBotFragment extends Fragment implements ChatBotView, View.OnClickListener, SpeechDelegate {

    public static final String TAG = "ChatBotFragment";
    private static final int REQ_CODE_SPEECH_INPUT = 1001;

    private Bundle bundle;
    public static String CHATBOT_CONFIG = "chatbot_config";
    private ChatBotFragmentListener mListener;

    private View view;
    private XEditText message;
    private ImageView send, back, hideChat, mic, mute;
    private TextView title, headline, info;
    private LinearLayout toolbar, empty, speechCont;
    private RecyclerView recyclerView, persistOption;
    private Button submit;
    private SpeechProgressView speechProgress;
    private ChatMessageAdapter chatMessageAdapter;
    private ChatBotPresenter chatBotPresenter;
    private ChatBotConfig chatBotConfig;
    private CompositeSubscription compositeSubscription;

    private boolean isAppJustOpened = true;

    public ChatBotFragment() {
        // Required empty public constructor
    }

    public static ChatBotFragment newInstance(ChatBotConfig chatBotConfig) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHATBOT_CONFIG, chatBotConfig);
        ChatBotFragment fragment = new ChatBotFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ChatBotFragment newInstanceWithDefaultConfig() {
        Bundle bundle = new Bundle();
        ChatBotConfig chatBotConfig = new ChatBotConfig();
        bundle.putParcelable(CHATBOT_CONFIG, chatBotConfig);
        ChatBotFragment fragment = new ChatBotFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void loadFragment(FragmentActivity activity, android.support.v4.app.Fragment f, int frameId) {
        activity.getSupportFragmentManager().beginTransaction().add(frameId, f, TAG).commitAllowingStateLoss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        compositeSubscription = new CompositeSubscription();

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        bundle = getArguments();
        if (bundle != null && bundle.containsKey(CHATBOT_CONFIG)) {
            chatBotConfig = bundle.getParcelable(CHATBOT_CONFIG);
        }

        chatBotPresenter = new ChatBotPresenter(this, new ChatBotModel(getActivity()));
//        chatBotPresenter.setChatBotConfig(chatBotConfig);

        Log.d("AuthUISettings", new Gson().toJson(chatBotConfig));
        Log.d("SHA1", Util.getCertificateSHA1Fingerprint(getActivity()));
        Log.d("Hash Key", Util.getKeyHash(getActivity()));

        bindView();
        setViewListeners();
        bindData();
        applyTheme();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatBotFragmentListener) {
            mListener = (ChatBotFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AuthUIFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void bindView() {
        speechCont = view.findViewById(R.id.speech_cont);
        speechProgress = view.findViewById(R.id.speech_progress);
        mute = view.findViewById(R.id.mute);
        message = view.findViewById(R.id.message);
        send = view.findViewById(R.id.send);
        mic = view.findViewById(R.id.mic);
        recyclerView = view.findViewById(R.id.recycler_view);
        title = view.findViewById(R.id.title);
        back = view.findViewById(R.id.back);
        toolbar = view.findViewById(R.id.toolbar);
        empty = view.findViewById(R.id.empty);
        headline = view.findViewById(R.id.headline);
        info = view.findViewById(R.id.info);
        submit = view.findViewById(R.id.submit);
        hideChat = view.findViewById(R.id.hide_chat);
        persistOption = view.findViewById(R.id.persist_option);

        chatBotPresenter.enableDisableSend(send, false);

        chatMessageAdapter = new ChatMessageAdapter(getActivity(), new ArrayList<ChatMessage>(), chatBotConfig.getMaterialTheme(), chatBotConfig.getAppLogo(), getScreenHeight());
        chatMessageAdapter.setItemClickListener(new IResultListener<View>() {
            @Override
            public void onResult(View view) {
                if (view.getTag() != null) {
                    if (view.getTag() instanceof BotMessage) {
                        BotMessage botMessage = (BotMessage) view.getTag();
                        switch (botMessage.getType()) {
                            case TEXT:
                                break;
                            case IMAGE:
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(botMessage.getValue()), "image/*");
                                startActivity(intent);
                                break;
                            case BUTTON:
                                break;
                            case VIDEO:
                                Intent i = new Intent(getActivity(), WebViewActivity.class);
                                i.putExtra(WebViewActivity.DATA, new WebViewData("", botMessage.getValue()));
                                startActivity(i);
                                break;
                        }
                    }
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatMessageAdapter);

        int[] heights = {40, 56, 38, 60, 35};
        speechProgress.setBarMaxHeightsInDp(heights);

    }

    private void setViewListeners() {
        send.setOnClickListener(this);
        mic.setOnClickListener(this);
        back.setOnClickListener(this);
        hideChat.setOnClickListener(this);
        mute.setOnClickListener(this);
        chatBotPresenter.onInputMessageChangeListener(message, send);

        chatMessageAdapter.setChildItemClickListener(new IResultListener<View>() {
            @Override
            public void onResult(View view) {

                if (view.getTag() != null) {
                    if (view.getTag() instanceof ChatOption) {
                        ChatOption chatOption = (ChatOption) view.getTag();
                        chatBotPresenter.sendSelectedOption(chatOption);
                    } else if (view.getTag() instanceof ChatButton) {
                        ChatButton chatButton = (ChatButton) view.getTag();
                        chatBotPresenter.sendSelectedOption(chatButton);
                    }
                } else {
                    Chat chat = (Chat) view.getTag(R.id.chat);
                    Log.d("Chat", new Gson().toJson(chat));
                    if (chat != null) {
                        ChatOption chatOption = (ChatOption) view.getTag(R.id.option);
                        if (chatOption != null) {
                            chatBotPresenter.sendSelectedOption(chatOption);
                            if (chat.getAnswerType() == AnswerType.OPTION) {
                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setId(chat.getId());
                                if (chat.getBotMessages() != null)
                                    chatMessage.setBotMessage(new Gson().toJson(chat.getBotMessages()));
                                chatMessage.setAnswerType(chat.getAnswerType().getDescription().toUpperCase());
                                chatMessage.setWebId(chat.getWebId());
                                chatMessage.setProjectId(chat.getProjectId());
                                chatBotPresenter.updateChat(chatMessage);
                                Log.d("ChatUpdated", new Gson().toJson(chat));
                            }
                        }
                    }
                }
            }
        });
    }

    public void bindData() {
        title.setText(chatBotConfig.getTitle());
        chatBotPresenter.bindViewModel(getActivity(), this);
    }

    private void applyTheme() {

        MaterialTheme materialTheme = chatBotConfig.getMaterialTheme();
        MaterialColor materialColor = materialTheme.getColor();

        switch (materialTheme) {
            case WHITE:
                toolbar.setBackgroundDrawable(Util.selectorBackground(getResources().getColor(materialColor.getLight()), getResources().getColor(materialColor.getLight()), false));
                title.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                send.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getDark()), android.graphics.PorterDuff.Mode.SRC_IN);
                mic.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getDark()), android.graphics.PorterDuff.Mode.SRC_IN);
                submit.setBackgroundDrawable(Util.selectorRoundedBackground(getResources().getColor(materialColor.getLight()), getResources().getColor(materialColor.getDark()), false));
                submit.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                back.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getPrimaryText()), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                toolbar.setBackgroundDrawable(Util.selectorBackground(getResources().getColor(materialColor.getRegular()), getResources().getColor(materialColor.getDark()), false));
                title.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                send.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getRegular()), android.graphics.PorterDuff.Mode.SRC_IN);
                mic.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getRegular()), android.graphics.PorterDuff.Mode.SRC_IN);
                submit.setBackgroundDrawable(Util.selectorRoundedBackground(getResources().getColor(materialColor.getRegular()), getResources().getColor(materialColor.getDark()), false));
                submit.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                back.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getWhite()), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    private void pushMessage(String msg) {
        chatBotPresenter.pushMessage(msg);
    }

    @Override
    public void showFCMNotSyncView() {
        empty.setVisibility(View.VISIBLE);
        headline.setText("FCM Token");
        info.setText("Your FCM Token is not synced, please sync now");
        submit.setText("Sync");
    }

    @Override
    public void showNoChatMessagesView() {
        if (chatMessageAdapter.getItemCount() == 0) {
            empty.setVisibility(View.VISIBLE);
            headline.setText("Messages");
            info.setText("Your messages will be shown here!");
            submit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMessagePushed() {

    }

    @Override
    public void onMessagePushFailed(String message) {
        Log.d("onMessagePushFailed", message);
    }

    @Override
    public void sendChat(String msg) {
        chatBotPresenter.sendChat(msg);
        message.setText("");
//        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
//        chatBotPresenter.startFakeIncomingMessageListener(msg);
        chatBotPresenter.startFakeTypingMessageListener();
    }

    @Override
    public void onChatViewModelUpdate(List<ChatMessage> chatMessages) {
        if (chatMessages.size() == 0) {
            showNoChatMessagesView();
        } else {
            final ChatMessage chatMessage = chatMessages.get(chatMessages.size() - 1);

            Chat chat = ChatMessage.copyProperties(chatMessage);
            if (mute.getAlpha() != 0.3f && !isAppJustOpened) {
                if (chat.isIncoming() && chat.getBotMessages() != null && chat.getBotMessages().size() > 0) {
                    Speech.getInstance().say(chat.getBotMessages().get(0).getValue());
                }
            }

            isAppJustOpened = false;

            chatMessageAdapter.addItems(chatMessages);
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

//            if (chatMessage.isPersist()) {
//                switch (chat.getAnswerType()) {
//                    case PERSIST_OPTION:
//                        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
//                        ChatMessageOptionAdapter chatMessageOptionAdapter = new ChatMessageOptionAdapter(getActivity(), new ArrayList<ChatOption>(), chatBotConfig.getMaterialTheme());
//                        chatMessageOptionAdapter.setItemClickListener(new IResultListener<View>() {
//                            @Override
//                            public void onResult(View view) {
//                                if (view.getTag() != null) {
//                                    if (view.getTag() instanceof ChatOption) {
//                                        ChatOption chatOption = (ChatOption) view.getTag();
//                                        chatMessage.setPersist(false);
//                                        chatBotPresenter.updateChat(chatMessage);
//                                        chatBotPresenter.sendSelectedOption(chatOption);
//
//                                    }
//                                }
//                            }
//                        });
//                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
//                        persistOption.setLayoutManager(gridLayoutManager);
//                        persistOption.setAdapter(chatMessageOptionAdapter);
//                        chatMessageOptionAdapter.addItems(chat.getOptions());
//                        persistOption.addItemDecoration(itemDecoration);
//                        persistOption.setVisibility(View.VISIBLE);
//                        break;
//                    default:
//
//                        break;
//                }
//            } else {
//
//            }
            if (empty.getVisibility() == View.VISIBLE)
                empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send) {
            chatBotPresenter.onSendClick(message.getText().toString());
        } else if (view.getId() == R.id.hide_chat) {
            mListener.onHideChat();
        } else if (view.getId() == R.id.mic) {
            Dexter.withActivity(getActivity())
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            startListening();
                            /* ... */
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            /* ... */
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();

        } else if (view.getId() == R.id.mute) {
            if (mute.getAlpha() == 0.3f) {
                mute.setAlpha(1.0f);
            } else {
                mute.setAlpha(0.3f);
                UiUtils.showSnackbar(getActivity().findViewById(android.R.id.content), "Sound for speech is muted", Snackbar.LENGTH_SHORT);
            }

        } else {
            mListener.onChatBotBackpressed();
        }
    }

    protected int getScreenHeight() {
        return getActivity().findViewById(android.R.id.content).getHeight();
    }

    private void startListening() {
        Speech.getInstance().stopTextToSpeech();
        mic.setVisibility(View.GONE);
        speechCont.setVisibility(View.VISIBLE);
        try {
            Speech.getInstance().startListening(speechProgress, this);
        } catch (SpeechRecognitionNotAvailable speechRecognitionNotAvailable) {
            speechNotSupportedDialog();
        } catch (GoogleVoiceTypingDisabledException e) {
            enableGoogleVoiceTyping();
        }

//        try {
//            // you must have android.permission.RECORD_AUDIO granted at this point
//
//            Speech.getInstance().startListening(speechProgress, new SpeechDelegate() {
//                @Override
//                public void onStartOfSpeech() {
//                    Log.i("speech", "speech recognition is now active");
//                }
//
//                @Override
//                public void onSpeechRmsChanged(float value) {
//                    Log.d("speech", "rms is now: " + value);
//                }
//
//                @Override
//                public void onSpeechPartialResults(List<String> results) {
//                    StringBuilder str = new StringBuilder();
//                    for (String res : results) {
//                        str.append(res).append(" ");
//                    }
//
//                    Log.i("speech", "partial result: " + str.toString().trim());
//                }
//
//                @Override
//                public void onSpeechResult(String result) {
//                    Log.i("speech", "result: " + result);
//                    speechCont.setVisibility(View.GONE);
//                    if (!Util.textIsEmpty(result)) {
//                        sendChat(result);
//                    } else {
//                        if (result.isEmpty()) {
//                            Speech.getInstance().say("Pardon please!");
//                        } else {
//                            Speech.getInstance().say(result);
//                        }
//                    }
//                }
//            });
//        } catch (SpeechRecognitionNotAvailable exc) {
//            Log.e("speech", "Speech recognition is not available on this device!");
//            // You can prompt the user if he wants to install Google App to have
//            // speech recognition, and then you can simply call:
//            //
//            // SpeechUtil.redirectUserToGoogleAppOnPlayStore(this);
//            //
//            // to redirect the user to the Google App page on Play Store
//        } catch (GoogleVoiceTypingDisabledException exc) {
//            Log.e("speech", "Google voice typing must be enabled!");
//        }
    }

    private void showSpeechNotSupportedDialog() {

    }

    @Override
    public void onStartOfSpeech() {

    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {

    }

    @Override
    public void onSpeechResult(String result) {
        speechCont.setVisibility(View.GONE);
        mic.setVisibility(View.VISIBLE);
        if (!Util.textIsEmpty(result)) {
            sendChat(result);
        } else {
            if (result.isEmpty()) {
                Speech.getInstance().say("Pardon please!");
            } else {
                Speech.getInstance().say(result);
            }
        }
    }


    public void speechNotSupportedDialog() {
        CustomPopoverView customPopoverView = CustomPopoverView.builder(getActivity())
                .withPositiveTitle("Yes")
                .withNegativeTitle("Cancel")
                .withTitle("Speech Not Supported")
                .withMessage("Speech recognition is not available on this device. Do you want to install Google app to have speech recognition?")
                .setDialogButtonClickListener(new CustomPopoverView.DialogButtonClickListener() {
                    @Override
                    public void positiveButtonClicked(View view, AlertDialog alertDialog) {
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(getActivity());
                    }

                    @Override
                    public void negativeButtonClicked(View view, AlertDialog alertDialog) {

                    }

                    @Override
                    public void neutralButtonClicked(View view, AlertDialog alertDialog) {

                    }
                })
                .build();

        customPopoverView.show();
    }

    public void enableGoogleVoiceTyping() {
        CustomPopoverView customPopoverView = CustomPopoverView.builder(getActivity())
                .withPositiveTitle("Ok")
                .withTitle("Google voice typing")
                .withMessage("Please enable Google Voice Typing to use speech recognition!")
                .setDialogButtonClickListener(new CustomPopoverView.DialogButtonClickListener() {
                    @Override
                    public void positiveButtonClicked(View view, AlertDialog alertDialog) {
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(getActivity());
                    }

                    @Override
                    public void negativeButtonClicked(View view, AlertDialog alertDialog) {

                    }

                    @Override
                    public void neutralButtonClicked(View view, AlertDialog alertDialog) {

                    }
                })
                .build();

        customPopoverView.show();
    }

}
