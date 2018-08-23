package com.jubi.ai.chatbot.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jubi.ai.chatbot.R;


public class ChatMessageCarouselViewHolder extends RecyclerView.ViewHolder {

    public TextView title, text;
    public ImageView image;
    public LinearLayout buttonsContainer;
    public TextView[] labels;
    public ChatMessageCarouselViewHolder(View itemView) {
        super(itemView);
        if (itemView != null) {
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
//            label2 = itemView.findViewById(R.id.label2);
//            label3 = itemView.findViewById(R.id.label3);
//            label4 = itemView.findViewById(R.id.label4);
//            label5 = itemView.findViewById(R.id.label5);
//            labels = new TextView[]{label1, label2, label3, label4, label5};
            image = itemView.findViewById(R.id.image);
            buttonsContainer = itemView.findViewById(R.id.buttons_container);
        }
    }

}
