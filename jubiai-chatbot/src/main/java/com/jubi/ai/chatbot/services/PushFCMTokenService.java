package com.jubi.ai.chatbot.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.networking.APIClient;
import com.jubi.ai.chatbot.networking.APIService;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;
import com.jubi.ai.chatbot.util.NetworkUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by saya on 24/05/17.
 */

public class PushFCMTokenService extends Service {

    public static final String TAG = "PushFCMTokenService";
    public static final String FCM_TOKEN = "fcm_token";

    APIService mApiInterface;
    PreferenceUtils mPreferenceUtil;

    private Context mContext;
    private CompositeSubscription compositeSubscription;
    private String fcmToken = "";

    public PushFCMTokenService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mPreferenceUtil = new PreferenceUtils(mContext);
        ChatBotConfig chatBotConfig = mPreferenceUtil.getChatBotConfig();
        mApiInterface = APIClient.getAdapterApiService(chatBotConfig.getHost());
        compositeSubscription = new CompositeSubscription();
        if (NetworkUtils.isNetworkAvailable(mContext))
            pushToken();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        fcmToken = intent.getStringExtra(FCM_TOKEN);
        return START_STICKY;
    }

    private void pushToken() {
        mPreferenceUtil.setFCMToken(fcmToken);
//        compositeSubscription.add(mApiInterface.pushToken(Util.uniqueDeviceID(mContext), fcmToken)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Response>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d(TAG, "completed");
//                        compositeSubscription.clear();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d(TAG, "onError " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(Response response) {
//                        Log.d(TAG, "onNext " + response.code());
//                        if (response.code() == 200) {
//                            mPreferenceUtil.setFCMTokenSaved();
//                        }
//                    }
//                }));

    }

}
