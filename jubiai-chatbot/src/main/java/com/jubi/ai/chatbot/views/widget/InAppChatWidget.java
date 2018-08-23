package com.jubi.ai.chatbot.views.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialColor;
import com.jubi.ai.chatbot.models.ChatBotConfig;
import com.jubi.ai.chatbot.persistence.PreferenceUtils;
import com.jubi.ai.chatbot.util.UiUtils;
import com.jubi.ai.chatbot.views.activity.ChatBotActivity;

public class InAppChatWidget extends RelativeLayout {

    public ImageView mProceed;
    public ImageView mIcChat;
    private Context context;
    private int mWidgetColor = 0xffffff00;

    public InAppChatWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initAttributes(attrs);
        setBackGroundColor();
    }

    public InAppChatWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        initAttributes(attrs);
        setBackGroundColor();
    }

    public InAppChatWidget(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.in_app_chat_widget, null);
        mProceed = view.findViewById(R.id.proceed);
        mIcChat = view.findViewById(R.id.ic_chat);
        mProceed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context != null) {
                    ChatBotActivity.launch(context);
                }
            }
        });
        addView(view);
    }

    private void initAttributes(@Nullable AttributeSet attrs) {
        TypedArray styleAttrs = getContext().obtainStyledAttributes(
                attrs, R.styleable.InAppChatWidget);
        if (styleAttrs.hasValue(R.styleable.InAppChatWidget_widgetColor)) {
//            String color = styleAttrs.getString(R.styleable.ChatInAppWidget_widget_color);
//            if(!Util.textIsEmpty(color) && String.valueOf(color.charAt(0)).equalsIgnoreCase("#")) {
//                mWidgetColor = Color.parseColor(color);
//            }

            mWidgetColor = styleAttrs.getColor(R.styleable.InAppChatWidget_widgetColor, 0xffffff00);
        }
    }

    public void setBackGroundColor() {
        try {
            if (context != null) {

                Log.d("mWidgetColor", mWidgetColor + "");

                if (mWidgetColor != 0xffffff00) {
                    mProceed.setBackground(UiUtils.getCircularGradient(mWidgetColor, mWidgetColor));
                } else {
                    PreferenceUtils preferenceUtils = new PreferenceUtils(context);
                    ChatBotConfig chatBotConfig = preferenceUtils.getChatBotConfig();
                    if (chatBotConfig != null) {
                        MaterialColor materialColor = chatBotConfig.getMaterialTheme().getColor();
                        mProceed.setBackground(UiUtils.getCircularGradient(context.getResources().getColor(materialColor.getRegular()), context.getResources().getColor(materialColor.getDark())));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
