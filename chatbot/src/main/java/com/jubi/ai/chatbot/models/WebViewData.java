package com.jubi.ai.chatbot.models;

import android.os.Parcel;
import android.os.Parcelable;

public class WebViewData implements Parcelable {
    private String title;
    private String url;

    public WebViewData(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public WebViewData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    protected WebViewData(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Creator<WebViewData> CREATOR = new Creator<WebViewData>() {
        @Override
        public WebViewData createFromParcel(Parcel source) {
            return new WebViewData(source);
        }

        @Override
        public WebViewData[] newArray(int size) {
            return new WebViewData[size];
        }
    };
}