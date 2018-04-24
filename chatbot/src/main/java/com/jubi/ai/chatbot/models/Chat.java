package com.jubi.ai.chatbot.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.jubi.ai.chatbot.enums.AnswerType;

import java.util.List;

public class Chat {

    private String webId;
    private String projectId;
    private List<BotMessage> botMessages;
    private List<ChatOption> options;
    private String answerType;
    private Boolean incoming;
    private Boolean persist;

    public Chat() {
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

    public List<BotMessage> getBotMessages() {
        return botMessages;
    }

    public void setBotMessages(List<BotMessage> botMessages) {
        this.botMessages = botMessages;
    }

    public List<ChatOption> getOptions() {
        return options;
    }

    public void setOptions(List<ChatOption> options) {
        this.options = options;
    }

    public AnswerType getAnswerType() {
        if (answerType.equalsIgnoreCase("persist-option")) {
            return AnswerType.PERSIST_OPTION;
        }
        return AnswerType.valueOf(answerType.toUpperCase());
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
}
