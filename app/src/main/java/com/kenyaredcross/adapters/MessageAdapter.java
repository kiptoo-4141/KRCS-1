package com.kenyaredcross.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.ContactUsMessage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<ContactUsMessage> messagesList;

    public MessageAdapter(List<ContactUsMessage> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        ContactUsMessage message = messagesList.get(position);
        holder.messageTextView.setText(message.getMessage());
        holder.userEmailTextView.setText(message.getUserEmail());
        holder.statusTextView.setText(message.getStatus());
        if (message.getReply() != null) {
            holder.replyTextView.setVisibility(View.VISIBLE);
            holder.replyTextView.setText(message.getReply());
        } else {
            holder.replyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView, userEmailTextView, statusTextView, replyTextView;

        public MessageViewHolder(View view) {
            super(view);
            messageTextView = view.findViewById(R.id.messageTextView);
            userEmailTextView = view.findViewById(R.id.userEmailTextView);
            statusTextView = view.findViewById(R.id.statusTextView);
            replyTextView = view.findViewById(R.id.replyTextView);
        }
    }
}
