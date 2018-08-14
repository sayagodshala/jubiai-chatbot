package com.jubi.ai.chatbot.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.activity.ChatBotActivity;

public class ChatHeadService extends Service {
    private WindowManager mWindowManager;
    private View mChatHeadView;
    private ChatBotConfig chatBotConfig;

    private final IBinder mBinder = new MyBinder();


    public ChatHeadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceUtils preferenceUtils = new PreferenceUtils(this);

        chatBotConfig = preferenceUtils.getChatBotConfig();

        //Inflate the chat head layout we created
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.chat_head, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int xPos = displaymetrics.widthPixels - 200;
        int yPos = displaymetrics.heightPixels - 400;

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = xPos;
        params.y = yPos;

        //Add the view to the window
        mWindowManager.addView(mChatHeadView, params);

        //Set the close button.
//        ImageView closeButton = (ImageView) mChatHeadView.findViewById(R.id.close_btn);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //close the service and remove the chat head from the window
//                stopSelf();
//            }
//        });

        //Drag and move chat head using user's touch action.


        ImageView chatHeadImage = (ImageView) mChatHeadView.findViewById(R.id.chat);
        ImageView close = (ImageView) mChatHeadView.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;
                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (initialX == params.x && initialY == params.y) {
                            //Open the chat conversation click.
                            Intent intent = ChatBotActivity.getLaunchIntent(ChatHeadService.this);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //close the service and remove the chat heads
                            stopSelf();
                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mChatHeadView, params);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });

        if (chatBotConfig != null && chatBotConfig.getMaterialTheme() != MaterialTheme.EARLY_SALARY) {
            chatHeadImage.setBackgroundDrawable(Util.drawCircle(getResources().getColor(chatBotConfig.getMaterialTheme().getColor().getRegular())));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }

    public class MyBinder extends Binder {
        public ChatHeadService getService() {
            return ChatHeadService.this;
        }
    }

} 