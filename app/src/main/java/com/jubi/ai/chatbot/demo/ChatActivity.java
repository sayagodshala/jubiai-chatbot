package com.jubi.ai.chatbot.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.views.activity.ChatBotActivity;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatBotActivity.setUp(this, chatBotConfig(), true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ChatBotActivity.CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            ChatBotActivity.initChatHead(this);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ChatBotConfig chatBotConfig() {
        ChatBotConfig chatBotConfig = new ChatBotConfig();
        chatBotConfig.setAppLogo(R.drawable.ic_early_salary_logo);
        chatBotConfig.setMaterialTheme(MaterialTheme.EARLY_SALARY);
        chatBotConfig.setTitle("Earl");
        chatBotConfig.setProjectId("JUBI15Q9uk_EarlySalaryUAT");
        chatBotConfig.setPath("android");
        chatBotConfig.setHost("http://early-salary-development.herokuapp.com");
        chatBotConfig.setFcmToken(FirebaseInstanceId.getInstance().getToken());
        return chatBotConfig;
    }

}
