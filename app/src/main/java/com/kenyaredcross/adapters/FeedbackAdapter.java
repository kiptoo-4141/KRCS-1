package com.kenyaredcross.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private final List<Feedback> feedbackList;
    private final String currentUserEmail;

    public FeedbackAdapter(List<Feedback> feedbackList, String currentUserEmail) {
        this.feedbackList = feedbackList;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.feedbackText.setText(feedback.getFeedback());
        holder.senderText.setText(feedback.getEmail());
        holder.dateText.setText(feedback.getDateSent());

        if (feedback.getEmail().equals(currentUserEmail)) {
            holder.feedbackText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView feedbackText, senderText, dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            feedbackText = itemView.findViewById(R.id.feedbackText);
            senderText = itemView.findViewById(R.id.senderText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}
