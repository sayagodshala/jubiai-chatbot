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
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.enums.MaterialTheme;
import com.jubi.ai.chatbot.listeners.IResultListener;
import com.jubi.ai.chatbot.util.BitmapTransform;
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

import java.net.MalformedURLException;
import java.net.URL;
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
        final Chat chat = ChatMessage.copyProperties(chatMessage);
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
                        if (!Util.textIsEmpty(botMessage.getValue())) {
                            view = (View) layoutInflater.inflate(R.layout.item_text, null);
                            TextView textView = view.findViewById(R.id.received);
                            textView.setText(botMessage.getValue());
                            textView.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                            textView.setTextColor(context.getResources().getColor(materialColor.getBlack()));
                            holder.fieldCont.addView(view);
                        }
                        break;
                    case AUDIO:
                        break;
                    case IMAGE:
                        if (!Util.textIsEmpty(botMessage.getValue())) {
                            view = (View) layoutInflater.inflate(R.layout.item_image, null);
                            ImageView imageView = view.findViewById(R.id.image);
                            LinearLayout imageCont = view.findViewById(R.id.image_cont);
                            imageCont.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                            if (botMessage.getValue().contains("http://") || botMessage.getValue().contains("https://")) {
                                if (Util.getFileExtensionByUrl(botMessage.getValue()).toLowerCase().contains("gif")) {
                                    Glide.with(context).asGif().load(botMessage.getValue()).into(imageView);
                                } else {
                                    Picasso.with(context).load(botMessage.getValue()).into(imageView);
                                }
                            } else {
                                Picasso.with(context).load(Uri.parse("file://" + botMessage.getValue())).into(imageView);
                            }
                            imageView.setTag(botMessage);
                            imageView.setOnClickListener(clickListener);
                            holder.fieldCont.addView(view);
                        }
                        break;
                    case VIDEO:
                        if (!Util.textIsEmpty(botMessage.getValue())) {
                            view = (View) layoutInflater.inflate(R.layout.item_youtube, null);
                            ImageView iv = view.findViewById(R.id.image);
                            iv.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                            view.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                            iv.setTag(botMessage);
                            URL url = Util.checkURL(botMessage.getValue());
                            if (url != null) {
                                if (url.getHost().contains("youtube") && !Util.textIsEmpty(url.getQuery())) {
                                    String vId = url.getQuery().substring(url.getQuery().lastIndexOf("=") + 1);
                                    Picasso.with(context).load("https://i.ytimg.com/vi/" + vId + "/hqdefault.jpg").into(iv);
                                }
                            }
                            iv.setOnClickListener(clickListener);
                            holder.fieldCont.addView(view);
                        }
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
                    if (chat.getOptions() != null && chat.getOptions().size() > 0) {
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
                        chatMessageOptionAdapter.addItems(chat);
                        recyclerView.addItemDecoration(itemDecoration);
                        holder.fieldCont.addView(recyclerView);
                    }

                    break;
                case PERSIST_OPTION:
//                    if (!chatMessage.isPersist()) {
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
                    chatMessageOptionAdapter.addItems(chat);
                    recyclerView.addItemDecoration(itemDecoration);
                    holder.fieldCont.addView(recyclerView);
//                    }
                    break;
                case GENERIC:
                    if (chat.getOptions() != null && chat.getOptions().size() > 0) {
                        chatMessageCarouselAdapter = new ChatMessageCarouselAdapter(context, chat.getOptions(), materialTheme);
                        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                        chatMessageCarouselAdapter.setItemClickListener(new IResultListener<View>() {
                            @Override
                            public void onResult(View view) {
                                mChildItemClickListener.onResult(view);
                            }
                        });
                        holder.carouselCont.setLayoutManager(layoutManager);
                        holder.carouselCont.setAdapter(chatMessageCarouselAdapter);
                    }
                    break;
                case TYPING:
                    holder.typing.setVisibility(View.VISIBLE);
                    Glide.with(context).asGif().load(R.drawable.dots).into(holder.typing);
                    break;
            }

        } else {
            for (int i = 0; i < botMessages.size(); i++) {
                final BotMessage botMessage = botMessages.get(i);
                switch (botMessage.getType()) {
                    case TEXT:
                        if (!Util.textIsEmpty(botMessage.getValue())) {
                            holder.sent.setText(botMessage.getValue());
                            holder.sent.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getChatBubbleRcvd()), context.getResources().getColor(materialColor.getChatBubbleRcvd()), false));
                            holder.sent.setTextColor(context.getResources().getColor(materialColor.getBlack()));
                        }
                        break;
                    case IMAGE:
                        if (!Util.textIsEmpty(botMessage.getValue())) {
                            holder.sent.setVisibility(View.GONE);
                            view = (View) layoutInflater.inflate(R.layout.item_image, null);
                            ImageView imageView = view.findViewById(R.id.image);
                            LinearLayout imageCont = view.findViewById(R.id.image_cont);
                            imageCont.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getRegular()), context.getResources().getColor(materialColor.getRegular()), false));
                            if (botMessage.getValue().contains("http://") || botMessage.getValue().contains("https://")) {
                                String path = botMessage.getValue();
                                String ext = Util.getFileExtensionByUrl(path).toLowerCase();
                                if (ext.contains("jpg")
                                        || ext.contains("jpeg")
                                        || ext.contains("png")) {
                                    Picasso.with(context).load(botMessage.getValue()).placeholder(R.drawable.placeholder).into(imageView);
                                } else if (ext.contains("gif")) {
                                    Glide.with(context).asGif().load(botMessage.getValue()).into(imageView);
                                } else if (ext.contains("pdf")) {
                                    imageView.setImageResource(R.drawable.pdf);
                                } else if (ext.contains("ppt") || ext.contains("pptx")) {
                                    imageView.setImageResource(R.drawable.ppt);
                                } else if (ext.contains("xls") || ext.contains("xlsx")) {
                                    imageView.setImageResource(R.drawable.xls);
                                } else if (ext.contains("doc") || ext.contains(".docx")) {
                                    imageView.setImageResource(R.drawable.doc);
                                }
                            }
                            imageView.setTag(botMessage);
                            imageView.setOnClickListener(clickListener);
                            holder.sentFieldCont.addView(view);
                            holder.sentFieldCont.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
            holder.receivedView.setVisibility(View.GONE);
        }
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
//                holder.brandLogo.setBackgroundDrawable(Util.drawCircle(context.getResources().getColor(materialColor.getDark())));
                holder.arrowSent.setColorFilter(ContextCompat.getColor(context, materialColor.getLight()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.arrowRcvd.setColorFilter(ContextCompat.getColor(context, materialColor.getChatBubbleRcvd()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.sent.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getLight()), context.getResources().getColor(materialColor.getLight()), false));
                holder.sent.setTextColor(context.getResources().getColor(materialColor.getBlack()));
                break;
            default:
                holder.sent.setBackground(Util.selectorRoundedBackground(context.getResources().getColor(materialColor.getRegular()), context.getResources().getColor(materialColor.getRegular()), false));
                holder.sent.setTextColor(context.getResources().getColor(materialColor.getWhite()));
                holder.arrowSent.setColorFilter(ContextCompat.getColor(context, materialColor.getRegular()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.arrowRcvd.setColorFilter(ContextCompat.getColor(context, materialColor.getChatBubbleRcvd()), android.graphics.PorterDuff.Mode.SRC_IN);
                holder.senderPic.setColorFilter(ContextCompat.getColor(context, materialColor.getLight()), android.graphics.PorterDuff.Mode.SRC_IN);
//                holder.brandLogo.setBackgroundDrawable(Util.drawCircle(context.getResources().getColor(materialColor.getRegular())));
                break;
        }
//        holder.sent.setTextColor(ContextCompat.getColor(context, materialColor.getWhite()));
    }
}
