package com.jubi.ai.chatbot.demo;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.jubi.ai.chatbot.ChatBotApp;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;


public class AndroidApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }


}
