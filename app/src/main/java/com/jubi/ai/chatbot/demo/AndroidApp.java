package com.jubi.ai.chatbot.demo;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.jubi.ai.chatbot.ChatBotApp;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;


public class AndroidApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Init chatbotapp with your choice of database name
//        ChatBotApp.init(this, "jubi_ai_production");

        // Init chatbotapp with default database name;
        ChatBotApp.init(this);

        Stetho.initializeWithDefaults(this);
    }


}
