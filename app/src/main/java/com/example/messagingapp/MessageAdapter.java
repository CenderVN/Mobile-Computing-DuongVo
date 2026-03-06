package com.example.messagingapp;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import java.io.File;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private String currentUsername;

    public MessageAdapter(List<Message> messages, String currentUsername) {
        this.messages = messages;
        this.currentUsername = currentUsername;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView topText;
        ImageView photo;
        LinearLayout container;
        CardView card;

        public ViewHolder(View v) {
            super(v);
            topText = v.findViewById(R.id.txtMsgTop);
            photo = v.findViewById(R.id.imgMsgPhoto);
            container = v.findViewById(R.id.msgContainer);
            card = v.findViewById(R.id.msgCard);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messages.get(position);
        Meme meme = msg.getContent();

        holder.topText.setText(meme.getToptext());
        
        String imgPath = meme.getImage();
        if (imgPath != null && imgPath.startsWith("/")) {
            Glide.with(holder.itemView.getContext()).load(new File(imgPath)).into(holder.photo);
        } else {
            Glide.with(holder.itemView.getContext()).load(imgPath).into(holder.photo);
        }

        if (msg.getSenderId().equals(currentUsername)) {
            holder.container.setGravity(Gravity.END);
            holder.card.setCardBackgroundColor(0xFF2E7D32);
        } else {
            holder.container.setGravity(Gravity.START);
            holder.card.setCardBackgroundColor(0xFF333333);
        }

        holder.itemView.setOnClickListener(v -> {
            String rawJson = new Gson().toJson(meme);
            Intent intent = new Intent(v.getContext(), MemeActivity.class);
            intent.putExtra("RAW_JSON", rawJson);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return messages.size(); }
}