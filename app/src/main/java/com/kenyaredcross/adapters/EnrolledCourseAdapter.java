package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.EnrolledCourseModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnrolledCourseAdapter extends FirebaseRecyclerAdapter<EnrolledCourseModel, EnrolledCourseAdapter.myViewHolder> {

    public EnrolledCourseAdapter(@NonNull FirebaseRecyclerOptions<EnrolledCourseModel> options) {
        super(options);  // Pass options to the constructor
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder myViewHolder, int position, @NonNull EnrolledCourseModel enrolledCourseModel) {
        // Set course details in view holder
        myViewHolder.courseTitle.setText(enrolledCourseModel.getTitle());
        myViewHolder.courseDescription.setText(enrolledCourseModel.getDescription());
        myViewHolder.certificationStatus.setText(enrolledCourseModel.getCertificationStatus());
        myViewHolder.courseDuration.setText(enrolledCourseModel.getDuration());
        myViewHolder.courseStatus.setText(enrolledCourseModel.getCourseStatus());

        // Load image using Glide
        Glide.with(myViewHolder.img.getContext())
                .load(enrolledCourseModel.getImage())
                .placeholder(R.drawable.baseline_question_mark_24)  // Placeholder image
                .circleCrop()
                .error(R.drawable.baseline_question_mark_24)  // Error image
                .into(myViewHolder.img);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.enrolled_courses_view, viewGroup, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView courseTitle, courseDescription, courseDuration, certificationStatus, courseStatus;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.courseimg);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            courseDuration = itemView.findViewById(R.id.courseDuration);
            certificationStatus = itemView.findViewById(R.id.certificationStatus);
            courseStatus = itemView.findViewById(R.id.courseStatus);  // Ensure correct ID for courseStatus
        }
    }
}
