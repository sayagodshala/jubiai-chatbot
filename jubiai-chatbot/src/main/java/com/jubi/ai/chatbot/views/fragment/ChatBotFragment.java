package com.jubi.ai.chatbot.views.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.appunite.appunitevideoplayer.PlayerActivity;
import com.google.gson.Gson;
import com.jubi.ai.chatbot.BuildConfig;
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
import com.jubi.ai.chatbot.util.AWSUtil;
import com.jubi.ai.chatbot.util.Constants;
import com.jubi.ai.chatbot.util.CustomPopoverView;
import com.jubi.ai.chatbot.util.FileAccessUtil;
import com.jubi.ai.chatbot.util.ItemOffsetDecoration;
import com.jubi.ai.chatbot.util.UiUtils;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.activity.WebViewActivity;
import com.jubi.ai.chatbot.views.adapter.ChatMessageAdapter;
import com.jubi.ai.chatbot.views.adapter.ChatMessageOptionAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.FilePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;
import com.xw.repo.XEditText;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.ui.SpeechProgressView;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;


public class ChatBotFragment extends Fragment implements ChatBotView, View.OnClickListener, SpeechDelegate, PopupMenu.OnMenuItemClickListener, ImagePickerCallback, FilePickerCallback {

    public static final String TAG = "ChatBotFragment";
    private static final int REQ_CODE_SPEECH_INPUT = 1001;
    private static final int CAMERA_REQUEST = 1001;
    private static final int GALLERY_REQUEST = 102;

    private Bundle bundle;
    public static String CHATBOT_CONFIG = "chatbot_config";
    private ChatBotFragmentListener mListener;

    private View view;
    private XEditText message;
    private ImageView send, back, hideChat, mic, mute, attachment, menu;
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
    private Uri cameraFile;
    private ProgressDialog mDialog;
    private AWSUtil awsUtil;
    private ImagePicker imagePicker;
    private FilePicker filePicker;
    private String whichIntent = "";

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
        imagePicker = new ImagePicker(this);
        awsUtil = new AWSUtil();
        compositeSubscription = new CompositeSubscription();

        mDialog = new ProgressDialog(getActivity());

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        bundle = getArguments();
        if (bundle != null && bundle.containsKey(CHATBOT_CONFIG)) {
            chatBotConfig = bundle.getParcelable(CHATBOT_CONFIG);
        }

        chatBotPresenter = new ChatBotPresenter(this, new ChatBotModel(getActivity()));

        Log.d("AuthUISettings", new Gson().toJson(chatBotConfig));
        Log.d("SHA1", Util.getCertificateSHA1Fingerprint(getActivity()));
        Log.d("Hash Key", Util.getKeyHash(getActivity()));

        bindView();
        setViewListeners();
        bindData();
        applyTheme();
//        pushMessage("get started");
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
        attachment = view.findViewById(R.id.attachment);
        hideChat = view.findViewById(R.id.hide_chat);
        persistOption = view.findViewById(R.id.persist_option);
        menu = view.findViewById(R.id.menu);

        chatBotPresenter.enableDisableSend(send, false);

        mute.setAlpha(0.3f);

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

                                String path = botMessage.getValue();
                                String ext = Util.getFileExtensionByUrl(path).toLowerCase();

                                if (ext.contains("jpg")
                                        || ext.contains("jpeg")
                                        || ext.contains("png")) {
                                    intent.setDataAndType(Uri.parse(path), "image/*");
                                } else if (ext.contains("pdf")) {
                                    intent.setDataAndType(Uri.parse(path), "application/pdf");
                                } else if (ext.contains("ppt") || ext.contains("pptx")) {
                                    intent.setDataAndType(Uri.parse(path), "application/vnd.ms-powerpoint");
                                } else if (ext.contains("xls") || ext.contains("xlsx")) {
                                    intent.setDataAndType(Uri.parse(path), "application/vnd.ms-excel");
                                } else if (ext.contains("doc") || ext.contains(".docx")) {
                                    intent.setDataAndType(Uri.parse(path), "application/msword");
                                }
                                startActivity(intent);
                                break;
                            case BUTTON:
                                break;
                            case VIDEO:
                                Intent i = new Intent(getActivity(), WebViewActivity.class);
                                URL url = Util.checkURL(botMessage.getValue());
                                if (url != null) {
                                    if (url.getHost().contains("youtube") && !Util.textIsEmpty(url.getQuery())) {
                                        String vId = url.getQuery().substring(url.getQuery().lastIndexOf("=") + 1);
                                        i.putExtra(WebViewActivity.DATA, new WebViewData("", vId));
                                    } else {
                                        i = PlayerActivity.getVideoPlayerIntent(getActivity(),
                                                botMessage.getValue(),
                                                "Video");
                                    }
                                }
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

        hideChat.setVisibility(View.GONE);

        attachment.setVisibility(chatBotConfig.isAttachmentRequired() ? View.VISIBLE : View.GONE);
        mic.setVisibility(chatBotConfig.isSpeechRequired() ? View.VISIBLE : View.GONE);

    }

    private void setViewListeners() {
        send.setOnClickListener(this);
        mic.setOnClickListener(this);
        back.setOnClickListener(this);
        hideChat.setOnClickListener(this);
        mute.setOnClickListener(this);
        attachment.setOnClickListener(this);
        menu.setOnClickListener(this);
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
                toolbar.setBackground(Util.selectorBackground(getResources().getColor(materialColor.getLight()), getResources().getColor(materialColor.getLight()), false));
                title.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                send.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getDark()), android.graphics.PorterDuff.Mode.SRC_IN);
                mic.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getDark()), android.graphics.PorterDuff.Mode.SRC_IN);
                submit.setBackground(Util.selectorRoundedBackground(getResources().getColor(materialColor.getLight()), getResources().getColor(materialColor.getDark()), false));
                submit.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                back.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getPrimaryText()), android.graphics.PorterDuff.Mode.SRC_IN);
                attachment.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getDark()), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
            default:
                toolbar.setBackground(Util.selectorBackground(getResources().getColor(materialColor.getRegular()), getResources().getColor(materialColor.getDark()), false));
                title.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                send.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getRegular()), android.graphics.PorterDuff.Mode.SRC_IN);
                mic.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getRegular()), android.graphics.PorterDuff.Mode.SRC_IN);
                submit.setBackground(Util.selectorRoundedBackground(getResources().getColor(materialColor.getRegular()), getResources().getColor(materialColor.getDark()), false));
                submit.setTextColor(getResources().getColor(materialColor.getPrimaryText()));
                back.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getWhite()), android.graphics.PorterDuff.Mode.SRC_IN);
                attachment.setColorFilter(ContextCompat.getColor(getActivity(), materialColor.getRegular()), android.graphics.PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    private void pushMessage(String msg) {
        chatBotPresenter.pushMessage(msg);
    }

    private void pushFileMessage(String url) {
        chatBotPresenter.pushImageMessage(url);
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
    public void onChatViewModelUpdate(List<ChatMessage> chatMessages) {
        if (chatMessages.size() == 0) {
            showNoChatMessagesView();
            pushMessage("get started");
        } else {
            final ChatMessage chatMessage = chatMessages.get(chatMessages.size() - 1);

            Chat chat = ChatMessage.copyProperties(chatMessage);
            if (chatBotConfig.isSpeechRequired() && mute.getAlpha() != 0.3f && !isAppJustOpened) {
                if (chat.isIncoming() && chat.getBotMessages() != null && chat.getBotMessages().size() > 0) {
                    StringBuilder forSpeech = new StringBuilder();
                    for (BotMessage bot : chat.getBotMessages()) {
                        if (bot.getValue().contains("http://") || bot.getValue().contains("https://")) {
                            // todo
                        } else {
                            forSpeech.append(bot.getValue());
                        }
                    }
                    if (!Util.textIsEmpty(forSpeech.toString()))
                        Speech.getInstance().say(forSpeech.toString());
                }
            }

            isAppJustOpened = false;

            chatMessageAdapter.addItems(chatMessages);
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            if (empty.getVisibility() == View.VISIBLE)
                empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send) {
            chatBotPresenter.sendChat(message.getText().toString());
            message.setText("");
            chatBotPresenter.startFakeTypingMessageListener();
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
        } else if (view.getId() == R.id.attachment) {
            whichIntent = "";
            PopupMenu popup = new PopupMenu(getActivity(), attachment);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.attachment);
            popup.show();

        } else if (view.getId() == R.id.menu) {
            PopupMenu popup = new PopupMenu(getActivity(), menu);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.persistent_menu);
            popup.show();
        } else {
            mListener.onChatBotBackpressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", requestCode + " - " + resultCode + " - " + data);
        if (!Util.textIsEmpty(whichIntent) && whichIntent.equalsIgnoreCase("camera")) {
            File file = new File(cameraFile.getPath());
            if (file.exists()) {
                uploadWithTransferUtility(file);
            } else {
                UiUtils.showToast(getActivity(), "Some problem occurred while capturing picture!");
            }
        } else if (!Util.textIsEmpty(whichIntent) && (whichIntent.equalsIgnoreCase("gallery") || whichIntent.equalsIgnoreCase("document")) && resultCode == -1 && data != null) {
            String realPath = FileAccessUtil.getPathFromUri(getActivity(), data.getData());
            if (!Util.textIsEmpty(realPath)) {
                File file = new File(realPath);
                if (file.exists()) {
                    uploadWithTransferUtility(file);
                } else {
                    UiUtils.showToast(getActivity(), "Some problem occurred while selecting document!");
                }
            } else {
                if (data.getData().toString().contains("google.android.apps.docs")) {
                    UiUtils.showToast(getActivity(), "Some problem occurred while selecting google drive document");
                } else {
                    UiUtils.showToast(getActivity(), "Some problem occurred while selecting document");
                }
            }
        } else {
            UiUtils.showToast(getActivity(), "Some thing went wrong");
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
            chatBotPresenter.sendChat(message.getText().toString());
            message.setText("");
            chatBotPresenter.startFakeTypingMessageListener();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.start_over) {
            pushMessage("get started");
            chatBotPresenter.startFakeTypingMessageListener();
            return true;
        } else if (item.getItemId() == R.id.cancel) {
            pushMessage("cancel");
            return true;
        } else if (item.getItemId() == R.id.camera) {
            Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                        whichIntent = "camera";

                        String fileName = BuildConfig.APPLICATION_ID + "_"
                                + String.valueOf(System.currentTimeMillis()) + ".jpg";
                        cameraFile = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory()
                                + "/"
                                + BuildConfig.APPLICATION_ID, fileName));

                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFile);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
            return true;
        } else if (item.getItemId() == R.id.gallery) {
            Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        whichIntent = "gallery";
                        pickImageSingle();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
            return true;
        } else if (item.getItemId() == R.id.document) {
            Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        whichIntent = "document";
                        pickFilesSingle();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
            return true;
        } else {
            pushMessage("talk to agent");
            return true;
        }
    }

    private void uploadWithTransferUtility(final File file) {
        setProgressDialog("Uploading", "Uploading dialog");
        final String extension = file.getName().substring(file.getName().lastIndexOf("."));
        final String fileName = System.currentTimeMillis() + extension;
        final String absoluteFileName = "https://s3.ap-south-1.amazonaws.com/mobile-dev-jubi/" + fileName;

        TransferUtility transferUtility = awsUtil.getTransferUtility(getActivity());

        TransferObserver uploadObserver =
                transferUtility.upload(
                        Constants.AWS.BUCKET_NAME, fileName, file, CannedAccessControlList.PublicRead);

        Log.d("AWSConfiguration path", uploadObserver.getAbsoluteFilePath());

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                switch (state) {
                    case COMPLETED:
                        mDialog.dismiss();
                        chatBotPresenter.imageChatMessage(absoluteFileName);
                        pushFileMessage(absoluteFileName);
                        break;
                    case FAILED:
                        mDialog.dismiss();
                        break;
                }
                Log.d("AWSFileUpload Stats", state.name() + " - " + id);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                mDialog.setProgress(percentDone);

                Log.d("AWSFileUpload Prog", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Log.d("AWSFileUpload Exc", ex.getMessage());
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d("YourActivity", "Bytes Transferrred: " + uploadObserver.getBytesTransferred());
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());
    }

    public void setProgressDialog(String title, String message) {
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setTitle(title);
        mDialog.setMessage(message);
        mDialog.show();
    }

    public void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.setDebugglable(true);
        imagePicker.setFolderName("Random");
        imagePicker.ensureMaxSize(500, 500);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        Bundle bundle = new Bundle();
        bundle.putInt("android.intent.extras.CAMERA_FACING", 1);
        imagePicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_PUBLIC_DIR);
        imagePicker.pickImage();
    }

    private void pickFilesSingle() {
        filePicker = getFilePicker();
        filePicker.setMimeType("application/*");
        filePicker.pickFile();
    }

    private FilePicker getFilePicker() {
        filePicker = new FilePicker(this);
        filePicker.setFilePickerCallback(this);
        return filePicker;
    }

    @Override
    public void onImagesChosen(List<ChosenImage> list) {

    }

    @Override
    public void onError(String s) {
        UiUtils.showToast(getActivity(), s);
    }

    @Override
    public void onFilesChosen(List<ChosenFile> list) {

    }
}
