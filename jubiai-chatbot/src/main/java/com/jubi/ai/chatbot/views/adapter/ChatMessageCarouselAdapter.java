package com.jubi.ai.chatbot.views.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialColor;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.listeners.IResultListener;
import com.jubi.ai.chatbot.models.BotMessage;
import com.jubi.ai.chatbot.models.Chat;
import com.jubi.ai.chatbot.models.ChatButton;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.persistence.ChatMessage;
import com.jubi.ai.chatbot.util.ItemOffsetDecoration;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.viewholder.ChatMessageCarouselViewHolder;
import com.jubi.ai.chatbot.views.viewholder.ChatMessageViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageCarouselAdapter extends RecyclerView.Adapter<ChatMessageCarouselViewHolder> {

    private Context context;
    private List<ChatOption> items;
    private MaterialTheme materialTheme;
    private IResultListener<View> mItemClickListener;
    private Chat chat;

    public ChatMessageCarouselAdapter(Context context, List<ChatOption> items, MaterialTheme materialTheme) {
        this.context = context;
        this.items = items;
        this.materialTheme = materialTheme;
    }

    @Override
    public ChatMessageCarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_carousel, parent, false);
        return new ChatMessageCarouselViewHolder(view);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onResult(view);
            }
        }
    };

    @Override
    public void onBindViewHolder(ChatMessageCarouselViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ChatOption item = items.get(position);
        holder.title.setText(item.getTitle());
        if (!Util.textIsEmpty(item.getImage())) {
            holder.image.setVisibility(View.VISIBLE);
            Picasso.with(context).load(item.getImage()).into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (item.getButtons() != null && item.getButtons().size() > 0) {
            for (ChatButton button : item.getButtons()) {
                View view = (View) layoutInflater.inflate(R.layout.item_carousel_text, null);
                TextView label = view.findViewById(R.id.label);
                label.setText(button.getText());
                label.setTag(button);
                label.setOnClickListener(clickListener);
                holder.buttonsContainer.addView(view);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<ChatOption> items) {
        this.items = items;
        notifyDataSetChanged();
    }

//    private void applyTheme(ChatMessageViewHolder holder) {
//        MaterialColor materialColor = materialTheme.getColor();
//        switch (materialTheme) {
//            case WHITE:
//                holder.senderPic.setColorFilter(ContextCompat.getColor(context, materialColor.getDark()), android.graphics.PorterDuff.Mode.SRC_IN);
//                holder.brandLogo.setBackgroundDrawable(drawCircle(context.getResources().getColor(materialColor.getDark())));
//                break;
//            default:
//                holder.senderPic.setColorFilter(ContextCompat.getColor(context, materialColor.getLightGrey()), android.graphics.PorterDuff.Mode.SRC_IN);
//                holder.brandLogo.setBackgroundDrawable(drawCircle(context.getResources().getColor(materialColor.getRegular())));
//                break;
//        }
//
//
//
////        holder.sent.setTextColor(ContextCompat.getColor(context, materialColor.getWhite()));
//    }

    public void setItemClickListener(IResultListener<View> listener) {
        mItemClickListener = listener;
    }

    public static ShapeDrawable drawCircle(int color) {
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.getPaint().setColor(color);
        return oval;
    }

}
