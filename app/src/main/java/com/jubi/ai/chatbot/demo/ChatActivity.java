package com.jubi.ai.chatbot.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.services.ChatHeadService;
import com.jubi.ai.chatbot.views.activity.ChatBotActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ChatBotActivity.saveConfig(this, chatBotConfig());
        ChatBotActivity.checkOverlayPermsForWidget(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ChatBotActivity.CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
                ChatBotActivity.initChatHead(this);
            } else {
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ChatBotConfig chatBotConfig() {
        ChatBotConfig chatBotConfig = new ChatBotConfig();
        chatBotConfig.setAppLogo(R.drawable.ic_early_salary_logo);
        chatBotConfig.setMaterialTheme(MaterialTheme.EARLY_SALARY);
        chatBotConfig.setTitle("Earl");
        chatBotConfig.setProjectId("JUBIglSWd_Prudential");
        chatBotConfig.setHost("https://prudential-backend.herokuapp.com");
//        attachment by default is true
//        chatBotConfig.setAttachmentRequired(false);
//        speech by default is false
//        chatBotConfig.setSpeechRequired(true);
        chatBotConfig.setFcmToken(FirebaseInstanceId.getInstance().getToken());
        return chatBotConfig;
    }

}
