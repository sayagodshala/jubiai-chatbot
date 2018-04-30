package com.jubi.ai.chatbot.listeners;

public interface IResultListener<T> {

    void onResult(T t);
}