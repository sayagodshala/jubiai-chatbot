package com.jubi.ai.chatbot.models;

import com.jubi.ai.chatbot.enums.Type;

public class BotMessage {
    private int id;
    private String type;
    private String value;

    public BotMessage(int id, String type, String value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return Type.valueOf(type.toUpperCase());
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
