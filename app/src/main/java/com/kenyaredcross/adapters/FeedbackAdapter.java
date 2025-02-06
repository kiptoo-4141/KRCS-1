package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<Feedback> feedbackList;
    private String currentUserEmail;

    public FeedbackAdapter(List<Feedback> feedbackList, String currentUserEmail) {
        this.feedbackList = feedbackList;
        this.currentUserEmail = currentUserEmail;
    }

    @Override
    public FeedbackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback2, parent, false);
        return new FeedbackViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        // Make sure the TextView is properly initialized
        if (holder.feedbackTextView != null) {
            holder.feedbackTextView.setText(feedback.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView feedbackTextView;

        public FeedbackViewHolder(View itemView) {
            super(itemView);
            feedbackTextView = itemView.findViewById(R.id.feedbackText);  // Ensure this ID matches the XML
        }
    }
}
