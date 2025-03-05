package com.kenyaredcross.adapters;

import android.content.Context;
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
import com.kenyaredcross.domain_model.MyCompletedCoursesModel;

import java.util.List;

public class MyCompletedCoursesAdapter extends RecyclerView.Adapter<MyCompletedCoursesAdapter.ViewHolder> {
    private final Context context;
    private final List<MyCompletedCoursesModel> courseList;

    public MyCompletedCoursesAdapter(Context context, List<MyCompletedCoursesModel> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_completed_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCompletedCoursesModel course = courseList.get(position);

        holder.courseTitle.setText(course.getTitle());
        holder.courseDescription.setText(course.getDescription());
        holder.courseDuration.setText("Duration: " + course.getDuration());

        Glide.with(context).load(course.getImage()).into(holder.courseImage);

        // Show certificate download button only if certificationStatus is "yes"
        if (course.getCertificationStatus().equals("yes")) {
            holder.downloadCertificate.setVisibility(View.VISIBLE);
            holder.downloadCertificate.setOnClickListener(v -> {
                // Handle certificate download logic (e.g., open PDF link, generate certificate)
            });
        } else {
            holder.downloadCertificate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, courseDescription, courseDuration;
        ImageView courseImage;
        Button downloadCertificate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            courseDuration = itemView.findViewById(R.id.courseDuration);
            courseImage = itemView.findViewById(R.id.courseImage);
            downloadCertificate = itemView.findViewById(R.id.downloadCertificate);
        }
    }
}
