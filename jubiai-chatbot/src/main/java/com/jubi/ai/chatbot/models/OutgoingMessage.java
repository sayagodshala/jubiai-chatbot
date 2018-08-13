package com.jubi.ai.chatbot.models;

import com.google.gson.annotations.SerializedName;

public class OutgoingMessage {

    @SerializedName("androidId")
    private String androidId;

    @SerializedName("projectId")
    private String projectId;

    @SerializedName("lastAnswer")
    private String lastAnswer;

    @SerializedName("type")
    private String type;

    @SerializedName("url")
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
