package com.jubi.ai.chatbot.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jubi.ai.chatbot.R;


public class ChatMessageOptionViewHolder extends RecyclerView.ViewHolder {

    public TextView option;

    public ChatMessageOptionViewHolder(View itemView) {
        super(itemView);
        if(itemView != null) {
            option = itemView.findViewById(R.id.option);
        }
    }

}
