package com.jubi.ai.chatbot.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.listeners.IResultListener;
import com.jubi.ai.chatbot.models.Chat;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.viewholder.ChatMessageOptionViewHolder;

import java.util.List;

public class ChatMessageOptionAdapter extends RecyclerView.Adapter<ChatMessageOptionViewHolder> {

    private Context context;
    private List<ChatOption> items;
    private MaterialTheme materialTheme;
    private IResultListener<View> mItemClickListener;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onResult(view);
            }
        }
    };
    private Chat chat;

    public void setItemClickListener(IResultListener<View> listener) {
        mItemClickListener = listener;
    }

    public ChatMessageOptionAdapter(Context context, List<ChatOption> items, MaterialTheme materialTheme) {
        this.context = context;
        this.items = items;
        this.materialTheme = materialTheme;
    }

    @Override
    public ChatMessageOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_option, parent, false);
        return new ChatMessageOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessageOptionViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ChatOption chatOption = items.get(position);
        holder.option.setText(chatOption.getText());
        holder.option.setTag(R.id.chat, chat);
        holder.option.setTag(R.id.option, chatOption);
        holder.option.setOnClickListener(clickListener);
        holder.option.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialTheme.getColor().getWhite()), context.getResources().getColor(materialTheme.getColor().getRegular()), true));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(Chat chat) {
        this.chat = chat;
        this.items = chat.getOptions();
        notifyDataSetChanged();
    }
}
