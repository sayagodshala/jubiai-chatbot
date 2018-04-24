package com.jubi.ai.chatbot.views.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.listeners.IResultListener;
import com.jubi.ai.chatbot.util.ItemOffsetDecoration;
import com.jubi.ai.chatbot.enums.MaterialColor;
import com.jubi.ai.chatbot.models.BotMessage;
import com.jubi.ai.chatbot.models.Chat;
import com.jubi.ai.chatbot.models.ChatOption;
import com.jubi.ai.chatbot.persistence.ChatMessage;
import com.jubi.ai.chatbot.util.MyLinearLayoutManager;
import com.jubi.ai.chatbot.util.Util;
import com.jubi.ai.chatbot.views.viewholder.ChatMessageViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    private final int screenWidth;
    private Context context;
    private List<ChatMessage> chatMessages;
    private MaterialTheme materialTheme;
    private int appLogo;
    private ChatMessageOptionAdapter chatMessageOptionAdapter;
    private ChatMessageCarouselAdapter chatMessageCarouselAdapter;

    private IResultListener<View> mChildItemClickListener;
    private IResultListener<View> mItemClickListener;
    private View.OnClickListener childClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mChildItemClickListener != null) {
                mChildItemClickListener.onResult(view);
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onResult(view);
            }
        }
    };

    public void setChildItemClickListener(IResultListener<View> listener) {
        mChildItemClickListener = listener;
    }

    public void setItemClickListener(IResultListener<View> listener) {
        mItemClickListener = listener;
    }

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessages, MaterialTheme materialTheme, int appLogo, int screenWidth) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.materialTheme = materialTheme;
        this.appLogo = appLogo;
        this.screenWidth = screenWidth;
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_message, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        //holder.itemView.getLayoutParams().width = screenWidth;
        if (position == 0)
            holder.space.setVisibility(View.VISIBLE);
        else
            holder.space.setVisibility(View.GONE);
        ChatMessage chatMessage = chatMessages.get(position);
        Chat chat = ChatMessage.copyProperties(chatMessage);
        MaterialColor materialColor = materialTheme.getColor();
        List<BotMessage> botMessages = chat.getBotMessages() != null ? chat.getBotMessages() : new ArrayList<BotMessage>();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (chatMessage.isIncoming()) {
            holder.sentCont.setVisibility(View.GONE);
            for (int i = 0; i < botMessages.size(); i++) {
                final BotMessage botMessage = botMessages.get(i);
                switch (botMessage.getType()) {
                    case TEXT:
                        view = (View) layoutInflater.inflate(R.layout.item_text, null);
                        TextView textView = view.findViewById(R.id.received);
                        textView.setText(botMessage.getValue());
                        textView.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                        textView.setTextColor(context.getResources().getColor(materialColor.getBlack()));
                        holder.fieldCont.addView(view);
                        break;
                    case AUDIO:
                        break;
                    case IMAGE:
                        view = (View) layoutInflater.inflate(R.layout.item_image, null);
                        ImageView imageView = view.findViewById(R.id.image);
                        imageView.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                        Picasso.with(context).load(botMessage.getValue()).into(imageView);
//                        Glide.with(context).load(botMessage.getValue()).into(imageView);
                        view.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                        imageView.setTag(botMessage);
                        imageView.setOnClickListener(clickListener);
                        holder.fieldCont.addView(view);
                        break;
                    case VIDEO:
                        view = (View) layoutInflater.inflate(R.layout.item_youtube, null);
                        ImageView iv = view.findViewById(R.id.image);
                        iv.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                        Picasso.with(context).load("https://i.ytimg.com/vi/" + botMessage.getValue() + "/hqdefault.jpg").into(iv);
//                        Glide.with(context).load(botMessage.getValue()).into(imageView);
                        view.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                        iv.setTag(botMessage);
                        iv.setOnClickListener(clickListener);
                        holder.fieldCont.addView(view);
//                        view = (View) layoutInflater.inflate(R.layout.item_youtube_thumbnail, null);
//                        YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
//                        final YouTubeThumbnailLoader[] youTubeThumbnailLoader = new YouTubeThumbnailLoader[1];
//                        thumbnail.setTag(botMessage.getValue());
//                        thumbnail.initialize(Constants.Key.YOUTUBE, new YouTubeThumbnailView.OnInitializedListener() {
//                            @Override
//                            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader loader) {
//                                youTubeThumbnailView.setImageResource(R.mipmap.loading_thumbnail);
//                                Log.d("onInitializationSuccess", loader.toString());
//                                youTubeThumbnailLoader[0].setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
//                                    @Override
//                                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
//                                        Log.d("onThumbnailLoaded", s);
//                                    }
//
//                                    @Override
//                                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
//                                        Log.d("onThumbnailError", errorReason.name());
//                                    }
//                                });
//                                youTubeThumbnailLoader[0] = loader;
//                                youTubeThumbnailLoader[0].setVideo(String.valueOf(youTubeThumbnailView.getTag()));
//                            }
//
//                            @Override
//                            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//                                Log.d("onInitializationFailure", youTubeInitializationResult.name());
//                            }
//                        });
//                        holder.fieldCont.addView(view);
                        break;
                }
            }
            RecyclerView recyclerView;
            ItemOffsetDecoration itemDecoration = null;
            GridLayoutManager gridLayoutManager = null;
            switch (chat.getAnswerType()) {
                case TEXT:

                    break;
                case OPTION:
                    itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
                    recyclerView = (RecyclerView) layoutInflater.inflate(R.layout.recycler_view, null);
                    chatMessageOptionAdapter = new ChatMessageOptionAdapter(context, new ArrayList<ChatOption>(), materialTheme);
                    chatMessageOptionAdapter.setItemClickListener(new IResultListener<View>() {
                        @Override
                        public void onResult(View view) {
                            mChildItemClickListener.onResult(view);
                        }
                    });
                    gridLayoutManager = new GridLayoutManager(context, 2);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(chatMessageOptionAdapter);
                    chatMessageOptionAdapter.addItems(chat.getOptions());
                    recyclerView.addItemDecoration(itemDecoration);
                    holder.fieldCont.addView(recyclerView);
                    break;
                case PERSIST_OPTION:
                    if (!chatMessage.isPersist()) {
                        itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
                        recyclerView = (RecyclerView) layoutInflater.inflate(R.layout.recycler_view, null);
                        chatMessageOptionAdapter = new ChatMessageOptionAdapter(context, new ArrayList<ChatOption>(), materialTheme);
                        chatMessageOptionAdapter.setItemClickListener(new IResultListener<View>() {
                            @Override
                            public void onResult(View view) {
                                mChildItemClickListener.onResult(view);
                            }
                        });
                        gridLayoutManager = new GridLayoutManager(context, 2);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(chatMessageOptionAdapter);
                        chatMessageOptionAdapter.addItems(chat.getOptions());
                        recyclerView.addItemDecoration(itemDecoration);
                        holder.fieldCont.addView(recyclerView);
                    }
                    break;
                case GENERIC:
                    chatMessageCarouselAdapter = new ChatMessageCarouselAdapter(context, new ArrayList<ChatOption>(), materialTheme);
                    MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    chatMessageCarouselAdapter.setItemClickListener(new IResultListener<View>() {
                        @Override
                        public void onResult(View view) {
                            mChildItemClickListener.onResult(view);
                        }
                    });
                    holder.carouselCont.setLayoutManager(layoutManager);
                    holder.carouselCont.setAdapter(chatMessageCarouselAdapter);
                    chatMessageCarouselAdapter.addItems(chat.getOptions());
                    break;
                case TYPING:
                    holder.typing.setVisibility(View.VISIBLE);
                    Glide.with(context).asGif().load(R.drawable.dots).into(holder.typing);
                    break;
            }

        } else {
            holder.sent.setText(botMessages.get(0).getValue());
            holder.receivedView.setVisibility(View.GONE);
        }
        holder.brandLogo.setImageResource(appLogo);

        applyTheme(holder);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addItems(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        notifyDataSetChanged();
    }

    private void applyTheme(ChatMessageViewHolder holder) {
        MaterialColor materialColor = materialTheme.getColor();
        switch (materialTheme) {
            case WHITE:
                holder.senderPic.setColorFilter(ContextCompat.getColor(context, materialColor.getDark()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.brandLogo.setBackgroundDrawable(Util.drawCircle(context.getResources().getColor(materialColor.getDark())));
                holder.arrowSent.setColorFilter(ContextCompat.getColor(context, materialColor.getLight()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.arrowRcvd.setColorFilter(ContextCompat.getColor(context, materialColor.getChatBubbleRcvd()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.sent.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getLight()), context.getResources().getColor(materialColor.getLight()), false));
                holder.sent.setTextColor(context.getResources().getColor(materialColor.getBlack()));
                break;
            default:
                holder.sent.setBackgroundDrawable(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getRegular()), context.getResources().getColor(materialColor.getRegular()), false));
                holder.sent.setTextColor(context.getResources().getColor(materialColor.getWhite()));
                holder.arrowSent.setColorFilter(ContextCompat.getColor(context, materialColor.getRegular()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.arrowRcvd.setColorFilter(ContextCompat.getColor(context, materialColor.getChatBubbleRcvd()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.senderPic.setColorFilter(ContextCompat.getColor(context, materialColor.getLight()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.brandLogo.setBackgroundDrawable(Util.drawCircle(context.getResources().getColor(materialColor.getRegular())));
                break;
        }
//        holder.sent.setTextColor(ContextCompat.getColor(context, materialColor.getWhite()));
    }
}
