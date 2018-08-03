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

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ChatBotActivity.saveConfig(this, chatBotConfig());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initChatHead();
            finish();
        }
    }

    private void initChatHead() {
        Intent intent = new Intent(ChatActivity.this, ChatHeadService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initChatHead();
                finish();
            } else { //Permission is not available
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
