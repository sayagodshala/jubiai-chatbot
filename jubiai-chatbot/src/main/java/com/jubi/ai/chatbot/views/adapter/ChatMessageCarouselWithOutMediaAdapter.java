package com.jubi.ai.chatbot.views.adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.listeners.IResultListener;
import com.jubi.ai.chatbot.models.Chat;
import com.jubi.ai.chatbot.models.ChatButton;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.viewholder.ChatMessageCarouselWithOutMediaViewHolder;

import java.util.List;

public class ChatMessageCarouselWithOutMediaAdapter extends RecyclerView.Adapter<ChatMessageCarouselWithOutMediaViewHolder> {

    private Context context;
    private List<ChatOption> items;
    private MaterialTheme materialTheme;
    private IResultListener<View> mItemClickListener;
    private Chat chat;

    public ChatMessageCarouselWithOutMediaAdapter(Context context, List<ChatOption> items, MaterialTheme materialTheme) {
        this.context = context;
        this.items = items;
        this.materialTheme = materialTheme;
    }

    @Override
    public ChatMessageCarouselWithOutMediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_carousel_without_media, parent, false);
        return new ChatMessageCarouselWithOutMediaViewHolder(view);
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
    public void onBindViewHolder(ChatMessageCarouselWithOutMediaViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ChatOption item = items.get(position);
        holder.title.setText(item.getTitle());
        if (!Util.textIsEmpty(item.getText())) {
            holder.text.setText(item.getText());
        }

        holder.text.setVisibility(Util.textIsEmpty(item.getText()) ? View.GONE : View.VISIBLE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (item.getButtons() != null && item.getButtons().size() > 0) {
            for (ChatButton button : item.getButtons()) {
                View view = (View) layoutInflater.inflate(R.layout.item_carousel_text, null);
                TextView label = view.findViewById(R.id.label);
                label.setText(button.getText());
                label.setTag(button);
                label.setOnClickListener(clickListener);
                label.setTextColor(Util.textColorStates(context.getResources().getColor(materialTheme.getColor().getRegular()), context.getResources().getColor(materialTheme.getColor().getDark())));
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

    public void setItemClickListener(IResultListener<View> listener) {
        mItemClickListener = listener;
    }

    public static ShapeDrawable drawCircle(int color) {
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.getPaint().setColor(color);
        return oval;
    }

}
