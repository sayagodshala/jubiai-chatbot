package com.jubi.ai.chatbot.views.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fujiyuu75.sequent.Animation;
import com.fujiyuu75.sequent.Direction;
import com.fujiyuu75.sequent.Sequent;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.listeners.ChatBotFragmentListener;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;
import com.jubi.ai.chatbot.services.ChatHeadService;
import com.jubi.ai.chatbot.util.CustomPopoverView;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.fragment.ChatBotFragment;

import net.gotev.speech.Logger;
import net.gotev.speech.Speech;

import static rx.schedulers.Schedulers.start;

public class ChatBotActivity extends AppCompatActivity implements ChatBotFragmentListener, View.OnClickListener {
    ChatBotFragment chatBotFragment;
    public static String CHATBOT_CONFIG = "chatbot_config";

    LinearLayout chatStartCont, empty;
    ImageView start;
    Button submit;
    TextView chatDialog, headline, info;
    private ChatBotConfig chatBotConfig;
    private boolean widgetCancled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        chatBotConfig = new ChatBotConfig();

        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);

        chatStartCont = findViewById(R.id.chat_start_cont);
        start = findViewById(R.id.start);
        chatDialog = findViewById(R.id.chat_dialog);
        empty = findViewById(R.id.empty);
        headline = findViewById(R.id.headline);
        info = findViewById(R.id.info);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        PreferenceUtils preferenceUtils = new PreferenceUtils(this);
        if (preferenceUtils.getFCMToken().equalsIgnoreCase("")) {
            fcmTokenEmptyDialog();
        } else if (getIntent().getExtras() != null && getIntent().getParcelableExtra(CHATBOT_CONFIG) != null) {
            chatBotConfig = (ChatBotConfig) getIntent().getParcelableExtra(CHATBOT_CONFIG);
            if (chatBotConfig.getProjectId() != null && chatBotConfig.getHost() != null) {
                preferenceUtils.setChatBotConfig(chatBotConfig);
                startTimerForChatWidget();
                start.setOnClickListener(this);
                applyTheme();

            } else {
                chatBotConfigDialog();
            }
        } else {
            chatBotConfigDialog();
        }
    }

    public static Intent getLaunchIntent(Context context, ChatBotConfig chatBotConfig) {
        Intent intent = new Intent(context, ChatBotActivity.class);
        intent.putExtra(CHATBOT_CONFIG, chatBotConfig);
        return intent;
    }

    public static void saveConfig(Context context, ChatBotConfig chatBotConfig) {
        PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        preferenceUtils.setChatBotConfig(chatBotConfig);
    }

    @Override
    public void onChatBotBackpressed() {

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

    private void startTimerForCheckingFCMToken() {
        new CountDownTimer(5000, 1000) {
            public int counter = 0;

            public void onTick(long millisUntilFinished) {
                counter += 1;
                if (counter == 1) {
                    chatStartCont.setVisibility(View.VISIBLE);
                    start.setVisibility(View.VISIBLE);
                } else if (counter == 2) {
                    chatDialog.setVisibility(View.VISIBLE);
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
        } else {
            finish();
        }
    }

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

    public void chatBotConfigDialog() {
        empty.setVisibility(View.VISIBLE);
        Util.defaultAnimateView(this, empty);
        headline.setText(chatBotConfig.getTitle());
        headline.setText("ChatBot is not configured properly, please check settings?");
        submit.setText("Close");

    }

    public void fcmTokenEmptyDialog() {
        empty.setVisibility(View.VISIBLE);
        Util.defaultAnimateView(this, empty);
        headline.setText(chatBotConfig.getTitle());
        headline.setText("FCM Token for this device not found, Please check firebase integration!");
        submit.setText("Close");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        Speech.getInstance().shutdown();
        super.onDestroy();
    }

    private void initChatHead() {
        Intent intent = new Intent(this, ChatHeadService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initChatHead();
    }

    private void changeStatusBarColor(){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(chatBotConfig.getMaterialTheme().getColor().getStatusBar()));
        }
    }

}
