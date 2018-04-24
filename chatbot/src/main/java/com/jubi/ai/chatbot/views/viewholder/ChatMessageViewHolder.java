package com.jubi.ai.chatbot.views.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jubi.ai.chatbot.R;


public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    public View space;
    public TextView sent;
    public RelativeLayout sentCont;
    public ImageView image, brandLogo, senderPic, typing, arrowSent, arrowRcvd;
    public LinearLayout fieldCont;
    public RecyclerView carouselCont;
    public RelativeLayout receivedView;

    public ChatMessageViewHolder(View itemView) {
        super(itemView);
        if(itemView != null) {
            space = itemView.findViewById(R.id.space);
            sent = itemView.findViewById(R.id.sent);
            brandLogo = itemView.findViewById(R.id.brand_logo);
            senderPic = itemView.findViewById(R.id.sender_pic);
            sentCont = itemView.findViewById(R.id.sent_cont);
            receivedView = itemView.findViewById(R.id.received_view);
            fieldCont = itemView.findViewById(R.id.field_cont);
            carouselCont = itemView.findViewById(R.id.carousel_cont);
            typing = itemView.findViewById(R.id.typing);
            arrowSent = itemView.findViewById(R.id.arrow_sent);
            arrowRcvd = itemView.findViewById(R.id.arrow_rcvd);
        }
    }

}
