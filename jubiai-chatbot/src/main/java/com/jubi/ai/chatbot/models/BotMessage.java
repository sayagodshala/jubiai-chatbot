package com.jubi.ai.chatbot.models;

import com.google.gson.annotations.SerializedName;
import com.jubi.ai.chatbot.enums.Type;

public class BotMessage {
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private String type;
    @SerializedName("value")
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
