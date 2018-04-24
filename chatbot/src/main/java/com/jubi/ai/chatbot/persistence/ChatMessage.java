package com.jubi.ai.chatbot.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jubi.ai.chatbot.enums.AnswerType;
import com.jubi.ai.chatbot.models.BotMessage;
import com.jubi.ai.chatbot.models.Chat;
import com.jubi.ai.chatbot.models.ChatOption;

import java.util.Collection;
import java.util.List;

@Entity(tableName = "chat")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "web_id")
    private String webId;

    @ColumnInfo(name = "project_id")
    private String projectId;

    @ColumnInfo(name = "bot_message")
    private String botMessage;

    @ColumnInfo(name = "options")
    private String options;

    @ColumnInfo(name = "answer_type")
    private String answerType;

    @ColumnInfo(name = "incoming")
    private Boolean incoming = true;

    @ColumnInfo(name = "persist")
    private Boolean persist = true;

    public ChatMessage() {
    }

    public String getWebId() {
        return webId;
    }

    public void setWebId(String webId) {
        this.webId = webId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBotMessage() {
        return botMessage;
    }

    public void setBotMessage(String botMessage) {
        this.botMessage = botMessage;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public Boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(Boolean incoming) {
        this.incoming = incoming;
    }

    public Boolean isPersist() {
        return persist;
    }

    public void setPersist(Boolean persist) {
        this.persist = persist;
    }

    public static Chat copyProperties(ChatMessage chatMessage) {
//        Log.d("ChatMessage", new Gson().toJson(chatMessage));
        Chat chat = new Chat();
        chat.setIncoming(chatMessage.isIncoming());
        chat.setProjectId(chatMessage.getProjectId());
        chat.setWebId(chatMessage.getWebId());
        chat.setAnswerType(chatMessage.getAnswerType());
        chat.setBotMessages((List<BotMessage>) new Gson().fromJson(chatMessage.getBotMessage(), new TypeToken<Collection<BotMessage>>() {}.getType()));
        if(chatMessage.getOptions() != null)
            chat.setOptions((List<ChatOption>) new Gson().fromJson(chatMessage.getOptions(), new TypeToken<Collection<ChatOption>>() {}.getType()));
        return chat;
    }

}
