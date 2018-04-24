package com.jubi.ai.chatbot.models;

public class BasicResponse {
    private String status;
    private String error;

    public BasicResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
