package com.jubi.ai.chatbot.models;

public class OutgoingMessage {

    private String androidId;
    private String projectId;
    private String lastAnswer;
    private String type = "attachment";
    private String url;

    public OutgoingMessage() {
    }

    public OutgoingMessage(String androidId, String projectId, String lastAnswer) {
        this.androidId = androidId;
        this.projectId = projectId;
        this.lastAnswer = lastAnswer;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getLastAnswer() {
        return lastAnswer;
    }

    public void setLastAnswer(String lastAnswer) {
        this.lastAnswer = lastAnswer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
