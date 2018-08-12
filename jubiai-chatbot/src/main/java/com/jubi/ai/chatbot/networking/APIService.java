package com.jubi.ai.chatbot.networking;

import com.jubi.ai.chatbot.models.BasicResponse;
import com.jubi.ai.chatbot.models.OutgoingMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by sayagodshala on 31/10/17.
 */
public interface APIService {

    @POST("{domainPath}/{projectName}")
    Call<BasicResponse> send(@Path("domainPath") String domainPath, @Path("projectName") String projectName, @Body OutgoingMessage message);
}
