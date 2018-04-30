package com.jubi.ai.chatbot.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ChatMessageDao {
    @Query("SELECT * FROM chat")
    LiveData<List<ChatMessage>> getAllChats();

    @Insert
    void insertChat(ChatMessage... chats);

    @Query("SELECT * FROM chat WHERE answer_type LIKE :answerType LIMIT 1")
    ChatMessage findByAnswerType(String answerType);

    @Delete
    void deleteChatMessage(ChatMessage chatMessage);

    @Update
    int updateChat(ChatMessage chatMessage);

}
