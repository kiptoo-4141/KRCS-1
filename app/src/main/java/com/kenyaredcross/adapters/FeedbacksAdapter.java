package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.FeedbacksModel;
import java.util.List;

public class FeedbacksAdapter extends RecyclerView.Adapter<FeedbacksAdapter.ViewHolder> {

    private final List<FeedbacksModel> feedbackList;

    public FeedbacksAdapter(List<FeedbacksModel> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedbacksModel feedback = feedbackList.get(position);
        holder.senderText.setText("To: " + feedback.getReceiverEmail());
        holder.receiverText.setText("From: " + feedback.getSenderEmail());
        holder.messageText.setText(feedback.getMessage());
        holder.timestampText.setText(new java.util.Date(feedback.getTimestamp()).toString());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderText, messageText, timestampText, receiverText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.senderText);
            receiverText = itemView.findViewById(R.id.receiverText);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }
    }
}