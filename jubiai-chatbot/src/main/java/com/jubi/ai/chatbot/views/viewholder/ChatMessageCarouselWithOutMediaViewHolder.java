package com.jubi.ai.chatbot.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jubi.ai.chatbot.R;


public class ChatMessageCarouselWithOutMediaViewHolder extends RecyclerView.ViewHolder {

    public TextView title, text;
    public LinearLayout buttonsContainer;

    public ChatMessageCarouselWithOutMediaViewHolder(View itemView) {
        super(itemView);
        if (itemView != null) {
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            buttonsContainer = itemView.findViewById(R.id.buttons_container);
        }
    }

}
