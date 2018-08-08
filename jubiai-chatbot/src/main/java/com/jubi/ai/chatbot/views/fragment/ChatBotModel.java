package com.jubi.ai.chatbot.views.fragment;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.jubi.ai.chatbot.ChatBotApp;
import com.jubi.ai.chatbot.enums.AnswerType;
import com.jubi.ai.chatbot.enums.Type;
import com.jubi.ai.chatbot.models.BasicResponse;
import com.jubi.ai.chatbot.models.BotMessage;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.models.ChatButton;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.models.OutgoingMessage;
import com.jubi.ai.chatbot.networking.APIClient;
import com.jubi.ai.chatbot.networking.APIService;
import com.jubi.ai.chatbot.persistence.ChatMessage;
import com.jubi.ai.chatbot.persistence.JubiAIChatBotDatabase;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Response;
import rx.Observable;

public class ChatBotModel {

    private final ChatBotConfig chatBotConfig;
    private Context context;
    private JubiAIChatBotDatabase database;
    private PreferenceUtils preferenceUtils;
    private APIService apiService;

    public ChatBotModel(Context context) {
        this.context = context;
        database = ChatBotApp.getDatabase(context);
        preferenceUtils = new PreferenceUtils(context);
        chatBotConfig = preferenceUtils.getChatBotConfig();
        apiService = APIClient.getAdapterApiService(chatBotConfig.getHost());
    }

    public void insertChat(ChatMessage chatMessage) {
        deleteTypingMessage();
        database.chatMessageDao().insertChat(chatMessage);
    }

    public void updateChat(ChatMessage chatMessage) {
        deleteTypingMessage();
        database.chatMessageDao().updateChat(chatMessage);
    }

    public void insertChat(String chatMessage) {
        deleteTypingMessage();
        database.chatMessageDao().insertChat(makeChatFromMessage(chatMessage));
    }

    public void insertChat() {
        deleteTypingMessage();
        database.chatMessageDao().insertChat(typingChatMessage());
    }

    public void setChatBotConfig(ChatBotConfig chatBotConfig) {
        preferenceUtils.setChatBotConfig(chatBotConfig);
    }

    public boolean isGCMTokenSaved() {
        return preferenceUtils.isFCMTokenSaved();
    }

    Observable<Response<BasicResponse>> pushMessage(String message) {
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.setAndroidId(preferenceUtils.getFCMToken());
        outgoingMessage.setProjectId(chatBotConfig.getProjectId());
        outgoingMessage.setLastAnswer(message);
        return apiService.send(chatBotConfig.getPath(), chatBotConfig.getProjectId(), outgoingMessage);
    }

    Observable<Response<BasicResponse>> pushImageMessage(String url) {
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.setAndroidId(preferenceUtils.getFCMToken());
        outgoingMessage.setProjectId(chatBotConfig.getProjectId());
        outgoingMessage.setUrl(url);
        outgoingMessage.setType("attachment");
        return apiService.send(chatBotConfig.getPath(), chatBotConfig.getProjectId(), outgoingMessage);
    }


    public ChatMessage makeChatFromMessage(String msg) {
        List<BotMessage> messages = new ArrayList<>();
        messages.add(new BotMessage(0, Type.TEXT.name(), msg));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setProjectId(chatBotConfig.getProjectId());
        chatMessage.setWebId(chatBotConfig.getWebId());
        chatMessage.setAnswerType(AnswerType.TEXT.name());
        chatMessage.setBotMessage(new Gson().toJson(messages));
        chatMessage.setIncoming(false);
        return chatMessage;
    }

    public ChatMessage textChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setProjectId(chatBotConfig.getProjectId());
        chatMessage.setWebId(chatBotConfig.getWebId());
        chatMessage.setAnswerType(AnswerType.TEXT.name());

        List<BotMessage> botMessages = new ArrayList<>();
        botMessages.add(new BotMessage(2, Type.TEXT.name(), "Text based chat"));
        botMessages.add(new BotMessage(2, Type.TEXT.name(), "this is sample for text based chat"));

        chatMessage.setBotMessage(new Gson().toJson(botMessages));

        return chatMessage;
    }

    public ChatMessage textChatMessageWithCarouselOptions() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setProjectId(chatBotConfig.getProjectId());
        chatMessage.setWebId(chatBotConfig.getWebId());
        chatMessage.setAnswerType(AnswerType.GENERIC.name());

        List<BotMessage> botMessages = new ArrayList<>();
        botMessages.add(new BotMessage(2, Type.TEXT.name(), "Carousel based chat."));
        botMessages.add(new BotMessage(2, Type.TEXT.name(), "this is sample for carousel based chat"));


        List<ChatOption> chatOptions = new ArrayList<>();

        ChatOption chatOption = new ChatOption();
        chatOption.setTitle("First Carousel");
        chatOption.setText("Sample text for First carousel");
        chatOption.setImage("http://139.59.22.129/plantandmachinery/saya_website/images/pic10.png");
        List<ChatButton> chatButtons = new ArrayList<>();
        chatButtons.add(new ChatButton(Type.BUTTON.name(), "Know more", "responseDataTobackend"));
        chatButtons.add(new ChatButton(Type.BUTTON.name(), "Highlights", "responseDataTobackend"));
        chatOption.setButtons(chatButtons);

        ChatOption chatOption1 = new ChatOption();
        chatOption1.setTitle("Second Carousel");
        chatOption1.setText("Sample text for Second carousel");
        chatOption1.setImage("http://139.59.22.129/plantandmachinery/saya_website/images/box8.png");
        List<ChatButton> chatButtons1 = new ArrayList<>();
        chatButtons1.add(new ChatButton(Type.BUTTON.name(), "About", "responseDataTobackend"));
        chatButtons1.add(new ChatButton(Type.BUTTON.name(), "Detail", "responseDataTobackend"));
        chatOption1.setButtons(chatButtons1);

        ChatOption chatOption2 = new ChatOption();
        chatOption2.setTitle("Third Carousel");
        chatOption2.setText("Sample text for Third carousel");
        chatOption2.setImage("http://139.59.22.129/plantandmachinery/saya_website/images/ap.png");
        List<ChatButton> chatButtons2 = new ArrayList<>();
        chatButtons2.add(new ChatButton(Type.BUTTON.name(), "Confirm", "responseDataTobackend"));
        chatButtons2.add(new ChatButton(Type.BUTTON.name(), "Decline", "responseDataTobackend"));
        chatOption2.setButtons(chatButtons2);

        chatOptions.add(chatOption);
        chatOptions.add(chatOption1);
        chatOptions.add(chatOption2);

        chatMessage.setBotMessage(new Gson().toJson(botMessages));
        chatMessage.setOptions(new Gson().toJson(chatOptions));

        return chatMessage;
    }

    public ChatMessage textChatMessageWithOptions() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setProjectId(chatBotConfig.getProjectId());
        chatMessage.setWebId(chatBotConfig.getWebId());
        chatMessage.setAnswerType(AnswerType.OPTION.name());

        List<BotMessage> botMessages = new ArrayList<>();
        botMessages.add(new BotMessage(2, Type.TEXT.name(), "Options based chat."));
        botMessages.add(new BotMessage(2, Type.TEXT.name(), "This is sample for options based chat"));

        List<ChatOption> chatOptions = new ArrayList<>();
        chatOptions.add(new ChatOption("Yes", "yes"));
        chatOptions.add(new ChatOption("No", "no"));

        chatMessage.setBotMessage(new Gson().toJson(botMessages));
        chatMessage.setOptions(new Gson().toJson(chatOptions));

        return chatMessage;
    }

    public ChatMessage imageChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setProjectId(chatBotConfig.getProjectId());
        chatMessage.setWebId(chatBotConfig.getWebId());
        chatMessage.setAnswerType(AnswerType.TEXT.name());

        List<BotMessage> botMessages = new ArrayList<>();

        Random rand = new Random();
        int index = 0;

        index = rand.nextInt(2);

        String[] images = {"http://139.59.22.129/plantandmachinery/saya_website/images/box8.png", "http://139.59.22.129/plantandmachinery/saya_website/images/pic10.png", "http://139.59.22.129/plantandmachinery/saya_website/images/ap.png"};

        botMessages.add(new BotMessage(2, Type.IMAGE.name(), images[index]));

        chatMessage.setBotMessage(new Gson().toJson(botMessages));

        return chatMessage;
    }

    public ChatMessage cameraImageChatMessage(String url) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setProjectId(chatBotConfig.getProjectId());
        chatMessage.setWebId(chatBotConfig.getWebId());
        chatMessage.setAnswerType(AnswerType.TEXT.name());
        chatMessage.setIncoming(false);
        List<BotMessage> botMessages = new ArrayList<>();

        botMessages.add(new BotMessage(2, Type.IMAGE.name(), url));

        chatMessage.setBotMessage(new Gson().toJson(botMessages));

        return chatMessage;
    }

    public ChatMessage typingChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setProjectId(chatBotConfig.getProjectId());
        chatMessage.setWebId(chatBotConfig.getWebId());
        chatMessage.setAnswerType(AnswerType.TYPING.name());
        return chatMessage;
    }

    private void deleteTypingMessage() {
        ChatMessage[] chatMessage = database.chatMessageDao().findByAnswerType(AnswerType.TYPING.name());
        if (chatMessage != null) {
            database.chatMessageDao().deleteChatMessage(chatMessage);
        }
    }

}
