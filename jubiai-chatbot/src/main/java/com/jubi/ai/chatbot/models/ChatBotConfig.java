package com.jubi.ai.chatbot.models;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialColor;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.util.Util;

import java.util.Date;

public class ChatBotConfig implements Parcelable {

    private MaterialTheme materialTheme = MaterialTheme.BLUE;
    private int appLogo = R.mipmap.ic_launcher;
    private String webId = null;
    private String projectId = null;
    private String title = "Jubi.AI ChatBot";
    private boolean saveChat = true;
    private String regularColor = null;
    private String host = null;
    private String path = "android";
    private String fcmToken;
    private boolean speechRequired = false;
    private boolean attachmentRequired = true;

    public ChatBotConfig() {
    }

    public ChatBotConfig(MaterialTheme materialTheme, int appLogo) {
        this.materialTheme = materialTheme;
        this.appLogo = appLogo;
    }

    public MaterialTheme getMaterialTheme() {
        if (!Util.textIsEmpty(regularColor))
            setCustomTheme();
        return materialTheme;
    }

    public void setMaterialTheme(MaterialTheme materialTheme) {
        this.materialTheme = materialTheme;
    }

    public int getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(int appLogo) {
        this.appLogo = appLogo;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSaveChat() {
        return saveChat;
    }

    public void setSaveChat(boolean saveChat) {
        this.saveChat = saveChat;
    }

    public String getRegularColor() {
        return regularColor;
    }

    public void setRegularColor(String regularColor) {
        this.regularColor = regularColor;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCustomTheme() {
        materialTheme = MaterialTheme.CUSTOM;
        MaterialColor materialColor = materialTheme.getColor();
        materialColor.setRegular(Color.parseColor(regularColor));
        materialColor.setLight(getLighterShadeColor(Color.parseColor(regularColor)));
        materialColor.setDark(getDarkerShadeColor(Color.parseColor(regularColor)));
        materialTheme.setColor(materialColor);
    }

    public int getDarkerShadeColor(int c) {
        float[] hsv = new float[3];
        int color = c;
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.80f;
        color = Color.HSVToColor(hsv);
        return color;
    }

    public int getLighterShadeColor(int c) {
        float[] hsv = new float[3];
        int color = c;
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.35f;
        color = Color.HSVToColor(hsv);
        return color;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean isSpeechRequired() {
        return speechRequired;
    }

    public void setSpeechRequired(boolean speechRequired) {
        this.speechRequired = speechRequired;
    }

    public boolean isAttachmentRequired() {
        return attachmentRequired;
    }

    public void setAttachmentRequired(boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
    }

    public final static class Builder {
        ChatBotConfig chatBotConfig;

        public Builder(Context context) {
            chatBotConfig = new ChatBotConfig();
        }

        public ChatBotConfig setMaterialTheme(MaterialTheme materialTheme) {
            chatBotConfig.materialTheme = materialTheme;
            return chatBotConfig;
        }

        public ChatBotConfig setAppLogo(int appLogo) {
            chatBotConfig.appLogo = appLogo;
            return chatBotConfig;
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.materialTheme.name());
        dest.writeInt(this.appLogo);
        dest.writeString(this.webId);
        dest.writeString(this.projectId);
        dest.writeString(this.title);
        dest.writeByte(this.saveChat ? (byte) 1 : (byte) 0);
        dest.writeString(this.regularColor);
        dest.writeString(this.host);
        dest.writeString(this.path);
        dest.writeString(this.fcmToken);
        dest.writeByte(this.speechRequired ? (byte) 1 : (byte) 0);
        dest.writeByte(this.attachmentRequired ? (byte) 1 : (byte) 0);
    }

    protected ChatBotConfig(Parcel in) {
        this.materialTheme = MaterialTheme.valueOf(in.readString());
        this.appLogo = in.readInt();
        this.webId = in.readString();
        this.projectId = in.readString();
        this.title = in.readString();
        this.saveChat = in.readByte() != 0;
        this.regularColor = in.readString();
        this.host = in.readString();
        this.path = in.readString();
        this.fcmToken = in.readString();
        this.speechRequired = in.readByte() != 0;
        this.attachmentRequired = in.readByte() != 0;
    }

    public static final Creator<ChatBotConfig> CREATOR = new Creator<ChatBotConfig>() {
        @Override
        public ChatBotConfig createFromParcel(Parcel source) {
            return new ChatBotConfig(source);
        }

        @Override
        public ChatBotConfig[] newArray(int size) {
            return new ChatBotConfig[size];
        }
    };
}
