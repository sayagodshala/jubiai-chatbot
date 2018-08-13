package com.jubi.ai.chatbot.models;

import com.google.gson.annotations.SerializedName;

public class RestError {

    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;
    @SerializedName("error")
    private String error;

    public RestError(int status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public RestError() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
