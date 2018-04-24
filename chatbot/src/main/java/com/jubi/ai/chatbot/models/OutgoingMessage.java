package com.jubi.ai.chatbot.models;

public class OutgoingMessage {

    private String androidId;
    private String projectId;
    private String lastAnswer;

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
}
