package com.jubi.ai.chatbot.persistence;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.jubi.ai.chatbot.util.Constants;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.util.Util;

/**
 * Created by sayagodshala on 15/05/17.
 */

public class PreferenceUtils extends PreferenceHelper {

    public PreferenceUtils(Context context) {
        super(context);
    }

    public void setChatBotConfig(ChatBotConfig chatBotConfig) {
        addPreference(Constants.PreferenceKeys.CHATBOTCONFIG, new Gson().toJson(chatBotConfig));
    }

    public ChatBotConfig getChatBotConfig() {
        ChatBotConfig chatBotConfig = new ChatBotConfig();
        String raw = getString(Constants.PreferenceKeys.CHATBOTCONFIG, "");
        if(!raw.equalsIgnoreCase("")) {
            chatBotConfig = new Gson().fromJson(raw, ChatBotConfig.class);
        }
        return chatBotConfig;
    }

    public void setFCMToken(String token) {
        Log.d("setFCMToken", !Util.textIsEmpty(token) ? token : "empty");
        addPreference(Constants.PreferenceKeys.FCM_REGISTRATION_ID, token);
    }

    public void setFCMAPIKey(String apiKey) {
        Log.d("setFcmApiKey", !Util.textIsEmpty(apiKey) ? apiKey : "empty");
        addPreference(Constants.PreferenceKeys.FCM_API_KEY, apiKey);
    }

    public String getFCMToken() {
        String raw = getString(Constants.PreferenceKeys.FCM_REGISTRATION_ID, "");
        Log.d("GCM Token", !Util.textIsEmpty(raw) ? raw : "empty");
        return raw;
    }

    public String getFCMAPIKey() {
        String raw = getString(Constants.PreferenceKeys.FCM_API_KEY, "");
        Log.d("FCM APIKey", !Util.textIsEmpty(raw) ? raw : "empty");
        return raw;
    }

    public void setDBName(String dbName) {
        addPreference(Constants.PreferenceKeys.DB_NAME, dbName);
    }

    public String getDBName() {
        String raw = getString(Constants.PreferenceKeys.DB_NAME, "");
        Log.d("DB Name", !Util.textIsEmpty(raw) ? raw : "empty");
        return raw;
    }

    public void setFCMTokenSaved() {
        addPreference(Constants.PreferenceKeys.FCM_TOKEN_SAVED, true);
    }

    public boolean isFCMTokenSaved() {
        boolean raw = getBoolean(Constants.PreferenceKeys.FCM_TOKEN_SAVED, false);
        Log.d("GCM Token Saved", raw + "");
        return raw;
    }

    public void removeUserSession() {
//        String raw = getFCMToken();
        super.clearSession();
//        setFCMToken(raw);
    }
}
