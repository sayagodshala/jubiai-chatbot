package com.jubi.ai.chatbot.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ChatMessage.class}, version = 1)
public abstract class JubiAIChatBotDatabase extends RoomDatabase {

    private static JubiAIChatBotDatabase INSTANCE;

    public static JubiAIChatBotDatabase getInstance(Context context, String databaseName) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context, JubiAIChatBotDatabase.class, databaseName)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public abstract ChatMessageDao chatMessageDao();
}
