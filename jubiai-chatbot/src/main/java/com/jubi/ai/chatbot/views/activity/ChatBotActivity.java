package com.jubi.ai.chatbot.views.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.jubi.ai.chatbot.ChatBotApp;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.listeners.ChatBotFragmentListener;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;
import com.jubi.ai.chatbot.services.ChatHeadService;
import com.jubi.ai.chatbot.util.UiUtils;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.fragment.ChatBotFragment;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

import java.util.Locale;

public class ChatBotActivity extends AppCompatActivity implements ChatBotFragmentListener, View.OnClickListener {
    ChatBotFragment chatBotFragment;
    public static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    LinearLayout chatStartCont, empty;
    ImageView start;
    Button submit;
    TextView chatDialog, headline, info;
    private ChatBotConfig chatBotConfig;
    private boolean widgetCancled = false;
    private PreferenceUtils preferenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatBotConfig = new ChatBotConfig();
        AWSMobileClient.getInstance().initialize(this).execute();
        Speech.init(this, getPackageName());
        Speech.getInstance().setLocale(new Locale("en_IN"));
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
        chatStartCont = findViewById(R.id.chat_start_cont);
        start = findViewById(R.id.start);
        chatDialog = findViewById(R.id.chat_dialog);
        empty = findViewById(R.id.empty);
        headline = findViewById(R.id.headline);
        info = findViewById(R.id.info);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        preferenceUtils = new PreferenceUtils(this);
        chatBotConfig = preferenceUtils.getChatBotConfig();
        if (preferenceUtils.getFCMToken().equalsIgnoreCase("")) {
            fcmTokenEmptyDialog();
        } else if (chatBotConfig != null) {
            if (chatBotConfig.getProjectId() != null && chatBotConfig.getHost() != null) {
                preferenceUtils.setChatBotConfig(chatBotConfig);
                loadChatView();
                applyTheme();
            } else {
                chatBotConfigDialog();
            }
        } else {
            chatBotConfigDialog();
        }

        if (chatBotConfig.isWidgetRequired())
            closeChatHeadService(ChatHeadService.class);

    }

    public static Intent getLaunchIntent(Context context) {
        PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        Intent intent = new Intent(context, ChatBotActivity.class);
        return intent;
    }

    public static void launch(Context context) {
        PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        if (preferenceUtils.isChatBotConfigGood()) {
            Intent intent = new Intent(context, ChatBotActivity.class);
            ((Activity) context).startActivity(intent);
        } else {
            UiUtils.showToast(context, "Oops! Bot is not Configured properly");
        }
    }

    public static void setUp(Context context, ChatBotConfig chatBotConfig) {
        PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        preferenceUtils.setFCMToken(chatBotConfig.getFcmToken());
        preferenceUtils.setChatBotConfig(chatBotConfig);
        ChatBotApp.initDatabase(context);
        if (chatBotConfig.isWidgetRequired()) {
            checkOverlayPermsForWidget(context);
        }
    }

    @Override
    public void onChatBotBackpressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (chatBotConfig != null && chatBotConfig.isWidgetRequired())
            initChatHead(this);
    }

    @Override
    public void onHideChat() {
        widgetCancled = false;
        getSupportFragmentManager().beginTransaction().remove(chatBotFragment).commit();
        startTimerForChatWidget();
    }

    private void startTimerForChatWidget() {
        new CountDownTimer(5000, 1000) {
            public int counter = 0;

            public void onTick(long millisUntilFinished) {
                counter += 1;
                if (counter == 1) {
                    chatStartCont.setVisibility(View.VISIBLE);
                    start.setVisibility(View.VISIBLE);
                    chatDialog.setVisibility(View.VISIBLE);
                    Util.defaultAnimateView(ChatBotActivity.this, chatStartCont);
                }
            }

            public void onFinish() {
                if (!widgetCancled) {
                    loadChatView();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start) {
            widgetCancled = true;
            loadChatView();
        } else if (view.getId() == R.id.submit) {
            if (submit.getText().toString().equalsIgnoreCase("refresh")) {
                finish();
                startActivity(getIntent());
            } else {
                finish();
            }

        }
    }

    /**
     * Load chatfragment
     */
    private void loadChatView() {
        chatStartCont.setVisibility(View.GONE);
        start.setVisibility(View.GONE);
        chatDialog.setVisibility(View.GONE);
        chatBotFragment = ChatBotFragment.newInstance(chatBotConfig);
        ChatBotFragment.loadFragment(ChatBotActivity.this, chatBotFragment, R.id.frame);
    }

    private void applyTheme() {
        start.setBackgroundDrawable(Util.drawCircle(getResources().getColor(chatBotConfig.getMaterialTheme().getColor().getRegular())));
        changeStatusBarColor();
    }


    /**
     * Show chatbot config dialog if not configured properly
     */
    public void chatBotConfigDialog() {
        empty.setVisibility(View.VISIBLE);
        Util.defaultAnimateView(this, empty);
        headline.setText(chatBotConfig.getTitle());
        headline.setText("ChatBot is not configured properly, please check settings?");
        submit.setText("Close");

    }

    /**
     * Show FCM dialog if Device token not found
     */
    public void fcmTokenEmptyDialog() {
        empty.setVisibility(View.VISIBLE);
        Util.defaultAnimateView(this, empty);
        headline.setText(chatBotConfig.getTitle());
        headline.setText("FCM Token for this device not found, Please check firebase integration!");
        submit.setText("Refresh");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        try {
            Speech.getInstance().shutdown();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    private void closeChatHeadService(Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    stopService(new Intent(this, ChatHeadService.class));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(chatBotConfig.getMaterialTheme().getColor().getStatusBar()));
        }
    }

    public static void checkOverlayPermsForWidget(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            ((Activity) context).startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            Intent intent = new Intent(context, ChatHeadService.class);
            context.startService(intent);
        }
    }

    public static void initChatHead(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(context, ChatHeadService.class);
            context.startService(intent);
        }
    }

}
