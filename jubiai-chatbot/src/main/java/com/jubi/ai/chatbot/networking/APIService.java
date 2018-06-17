package com.jubi.ai.chatbot.networking;

import com.jubi.ai.chatbot.models.BasicResponse;
import com.jubi.ai.chatbot.models.OutgoingMessage;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by sayagodshala on 31/10/17.
 */

public interface APIService {

    @POST("{domainPath}/{projectName}")
    Observable<Response<BasicResponse>> send(@Path("domainPath") String domainPath, @Path("projectName") String projectName, @Body OutgoingMessage message);

    @FormUrlEncoded
    @POST("/push_token")
    Observable<Response<BasicResponse>> pushToken(@Field("device_id") String deviceId, @Field("device_token") String deviceToken);

}
