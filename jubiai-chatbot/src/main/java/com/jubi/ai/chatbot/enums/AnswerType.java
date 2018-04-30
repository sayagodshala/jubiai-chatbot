package com.jubi.ai.chatbot.enums;


public enum AnswerType {
    PERSIST_OPTION("persist-option"), OPTION("option"), GENERIC("generic"), TEXT("text"), TYPING("typing");

    private String description;

    AnswerType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
