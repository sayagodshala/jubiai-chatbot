package com.jubi.ai.chatbot.views.fragment;

import com.jubi.ai.chatbot.persistence.ChatMessage;

import java.util.List;

public interface ChatBotView {

    void showFCMNotSyncView();

    void showNoChatMessagesView();

    void onMessagePushed();

    void onMessagePushFailed(String message);

    void onChatViewModelUpdate(List<ChatMessage> chatMessages);

    void noChatsAvailable();

}
