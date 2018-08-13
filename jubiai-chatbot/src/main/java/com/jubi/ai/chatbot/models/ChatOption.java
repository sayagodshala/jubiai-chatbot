package com.jubi.ai.chatbot.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatOption {
    @SerializedName("title")
    private String title;
    @SerializedName("text")
    private String text;
    @SerializedName("data")
    private String data;
    @SerializedName("image")
    private String image;
    @SerializedName("buttons")
    private List<ChatButton> buttons;

    public ChatOption() {
    }

    public ChatOption(String text, String data) {
        this.text = text;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ChatButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<ChatButton> buttons) {
        this.buttons = buttons;
    }
}
