package com.jubi.ai.chatbot.util;

public class Constants {

    public interface PreferenceKeys {
        String CHATBOTCONFIG = "chatbotconfig";
        String FCM_REGISTRATION_ID = "fcm_registration_id";
        String FCM_API_KEY = "fcm_api_key";
        String FCM_TOKEN_SAVED = "fcm_token_saved";
        String DB_NAME = "db_name";
    }

    public interface Key {
        String YOUTUBE = "AIzaSyA6Lx5hAHtlFw57lNcsOhN9ePl0lytv6-4";
    }

    public interface HTTPStatusCodes {
        int OK = 200;
        int EMPTY = 204;
        int UNAUTHORIZED = 401;
        int NOT_FOUND = 404;
    }

    public interface AWS{
        public String COGNITO_POOL_ID = "ap-south-1:392c5499-a210-4b1d-b55a-b170cd1cd7fa";
        public String COGNITO_POOL_REGION = "ap-south-1";
        public String BUCKET_REGION = "ap-south-1";
        public String BUCKET_NAME = "mobile-dev-jubi";

    }
}
