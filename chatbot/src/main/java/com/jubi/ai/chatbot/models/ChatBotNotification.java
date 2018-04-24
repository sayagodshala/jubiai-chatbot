package com.jubi.ai.chatbot.models;

public class ChatBotNotification {

    private String webId;
    private String projectId;
    private String botMessage;
    private String answerType;
    private String options;
    private String contentTitle;
    private String message;
    private String tickerText;

    public ChatBotNotification() {
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

    public String getBotMessage() {
        return botMessage;
    }

    public void setBotMessage(String botMessage) {
        this.botMessage = botMessage;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTickerText() {
        return tickerText;
    }

    public void setTickerText(String tickerText) {
        this.tickerText = tickerText;
    }
}
