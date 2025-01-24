package com.kenyaredcross.activity;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.EnrollmentModel;

public class EnrollmentViewHolder extends RecyclerView.ViewHolder {
    TextView username, title;

    public EnrollmentViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
        title = itemView.findViewById(R.id.title);
    }

    public void bind(EnrollmentModel model) {
        username.setText(model.getUsername());
        title.setText(model.getTitle());
    }
}
