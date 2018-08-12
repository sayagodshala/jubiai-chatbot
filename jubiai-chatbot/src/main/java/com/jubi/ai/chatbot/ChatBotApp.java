package com.jubi.ai.chatbot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.jubi.ai.chatbot.enums.AnswerType;
import com.jubi.ai.chatbot.models.ChatBotNotification;
import com.jubi.ai.chatbot.persistence.ChatMessage;
import com.jubi.ai.chatbot.persistence.JubiAIChatBotDatabase;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;
import com.jubi.ai.chatbot.util.Util;

import java.util.Date;
import java.util.Map;

import rx.subscriptions.CompositeSubscription;

public class ChatBotApp {

    public static JubiAIChatBotDatabase getDatabase(Context context) {
        PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        JubiAIChatBotDatabase database = JubiAIChatBotDatabase.getInstance(context, preferenceUtils.getDBName());
        return database;
    }

    public static void initDatabase(Context context) {
        PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        String dbName = preferenceUtils.getDBName();
        if (dbName.equalsIgnoreCase("")) {
            dbName = "jubi_ai_" + String.valueOf(new Date().getTime());
            preferenceUtils.setDBName(dbName);
        }
        JubiAIChatBotDatabase database = JubiAIChatBotDatabase.getInstance(context, dbName);
    }

    public static boolean isChatBotMessage(Map<String, String> rawData) {
        boolean flag = false;
        if (rawData != null && rawData.size() > 0 && rawData.containsKey("botMessage")) {
            flag = true;
        }
        return flag;
    }

    public static void handleMessage(Context context, ChatBotNotification chatBotNotification) {
        Log.d("ChatBotApp", "Message data payload: " + new Gson().toJson(chatBotNotification));
        ChatMessage chatRoomObject = new ChatMessage();
        chatRoomObject.setWebId(chatBotNotification.getWebId());
        chatRoomObject.setProjectId(chatBotNotification.getProjectId());
        if (chatBotNotification.getBotMessage() != null)
            chatRoomObject.setBotMessage(chatBotNotification.getBotMessage());
        chatRoomObject.setAnswerType(chatBotNotification.getAnswerType());
        if (chatBotNotification.getOptions() != null)
            chatRoomObject.setOptions(chatBotNotification.getOptions());
        saveChatMessage(context, chatRoomObject);
    }

    public static ChatBotNotification copyPropertiesFromMap(Map<String, String> rawData) {
        ChatBotNotification chatBotNotification = new ChatBotNotification();
        chatBotNotification.setWebId(rawData.get("webId"));
        chatBotNotification.setProjectId(rawData.get("projectId"));
        String botMessage = rawData.get("botMessage");
        chatBotNotification.setBotMessage(botMessage);
        if (rawData.containsKey("answerType")) {
            chatBotNotification.setAnswerType(rawData.get("answerType").toUpperCase());
        }
        if (rawData.containsKey("options")) {
            chatBotNotification.setOptions(rawData.get("options"));
        }
        chatBotNotification.setMessage(rawData.get("message"));
        chatBotNotification.setContentTitle(rawData.get("contentTitle"));
        chatBotNotification.setTickerText(rawData.get("tickerText"));
        return chatBotNotification;
    }

    public static void saveChatMessage(Context context, ChatMessage chatMessage) {
        JubiAIChatBotDatabase database = getDatabase(context);

        ChatMessage[] typingMessage = database.chatMessageDao().findByAnswerType(AnswerType.TYPING.name());
        if (typingMessage != null) {
            database.chatMessageDao().deleteChatMessage(typingMessage);
        }
        database.chatMessageDao().insertChat(chatMessage);
    }

    public static void generateChatBotNotification(Context context, Class<?> cls, ChatBotNotification chatMessage, int icon) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = context.getString(R.string.notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(icon)
                        .setContentTitle(chatMessage.getContentTitle())
                        .setContentText(chatMessage.getMessage())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    chatMessage.getTickerText(),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1001 /* ID of notification */, notificationBuilder.build());
    }

}
