package com.jubi.ai.chatbot.models;

import com.jubi.ai.chatbot.enums.AnswerType;
import com.jubi.ai.chatbot.util.Util;

import java.util.List;

public class Chat {
    private int id;
    private String webId;
    private String projectId;
    private List<BotMessage> botMessages;
    private List<ChatOption> options;
    private String answerType;
    private Boolean incoming;
    private Boolean persist;

    public Chat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        if (!Util.textIsEmpty(answerType)) {
            if (answerType.equalsIgnoreCase("persist-option")) {
                return AnswerType.PERSIST_OPTION;
            }
            return AnswerType.valueOf(answerType.toUpperCase());
        }
        return AnswerType.GENERIC;
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
