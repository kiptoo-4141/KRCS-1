package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.ClassModel;

import java.util.List;

public class EnrollmentAdapter extends RecyclerView.Adapter<EnrollmentAdapter.EnrollmentViewHolder> {

    private final List<ClassModel> enrollmentList;
    private final EnrollmentListener listener;

    public interface EnrollmentListener {
        void onStatusUpdated(String email, String courseTitle, String status);
    }

    public EnrollmentAdapter(List<ClassModel> enrollmentList, EnrollmentListener listener) {
        this.enrollmentList = enrollmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EnrollmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enrollment, parent, false);
        return new EnrollmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnrollmentViewHolder holder, int position) {
        ClassModel model = enrollmentList.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return enrollmentList.size();
    }

    public class EnrollmentViewHolder extends RecyclerView.ViewHolder {

        private final ImageView enrollmentImageView;
        private final TextView studentNameTextView, courseTitleTextView;
        private final Button passButton, failButton;

        public EnrollmentViewHolder(@NonNull View itemView) {
            super(itemView);
            enrollmentImageView = itemView.findViewById(R.id.enrollmentImageView);
            studentNameTextView = itemView.findViewById(R.id.studentNameTextView);
            courseTitleTextView = itemView.findViewById(R.id.courseTitleTextView);
            passButton = itemView.findViewById(R.id.passButton);
            failButton = itemView.findViewById(R.id.failButton);
        }

        public void bind(ClassModel model) {
            studentNameTextView.setText(model.getUsername());
            courseTitleTextView.setText(model.getTitle());
            Glide.with(enrollmentImageView.getContext()).load(model.getImage()).into(enrollmentImageView);

            passButton.setOnClickListener(v -> listener.onStatusUpdated(model.getEmail(), model.getTitle(), "Passed"));
            failButton.setOnClickListener(v -> listener.onStatusUpdated(model.getEmail(), model.getTitle(), "Failed"));
        }
    }
}
