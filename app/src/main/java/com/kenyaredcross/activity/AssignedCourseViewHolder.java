package com.kenyaredcross.activity;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.AssignedCourseModel;

public class AssignedCourseViewHolder extends RecyclerView.ViewHolder {
    TextView courseTitle;

    public AssignedCourseViewHolder(@NonNull View itemView) {
        super(itemView);
        courseTitle = itemView.findViewById(R.id.courseTitle);
    }

    public void bind(AssignedCourseModel model) {
        courseTitle.setText(model.getCourseTitle());
    }
}
