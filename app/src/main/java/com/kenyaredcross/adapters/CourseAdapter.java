package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Courses;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private final List<Courses> courseList;

    public CourseAdapter(List<Courses> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Courses course = courseList.get(position);
        holder.title.setText(course.getTitle());
        holder.description.setText(course.getDescription());
        holder.duration.setText(course.getDuration());

        Glide.with(holder.itemView.getContext())
                .load(course.getImageLink())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, duration;
        ImageView imageView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.courseTitle);
            description = itemView.findViewById(R.id.courseDescription);
            duration = itemView.findViewById(R.id.courseDuration);
            imageView = itemView.findViewById(R.id.courseImage);
        }
    }
}
