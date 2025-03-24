package com.kenyaredcross.adapters;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.CourseResource2;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseResourceAdapter2 extends RecyclerView.Adapter<CourseResourceAdapter2.ViewHolder> {
    private Map<String, CourseResource2> courseResources;
    private List<String> courseIds; // List to store course IDs for filtering

    public CourseResourceAdapter2(Map<String, CourseResource2> courseResources) {
        this.courseResources = courseResources;
        this.courseIds = new ArrayList<>(courseResources.keySet()); // Initialize course IDs
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_resource2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String courseId = courseIds.get(position);
        CourseResource2 courseResource = courseResources.get(courseId);

        if (courseResource != null) {
            holder.courseId.setText("Course ID: " + courseId);
            holder.courseActivities.setText("Activities: " + String.join(", ", courseResource.getCourseActivities()));
            holder.courseOutlines.setText("Outlines: " + String.join(", ", courseResource.getCourseOutlines()));
            holder.courseStructures.setText("Structures: " + String.join(", ", courseResource.getCourseStructures()));
            holder.resourceLinks.setText("Resource Links: " + String.join(", ", courseResource.getResourceLinks()));

            // Make resource links clickable
            holder.resourceLinks.setOnClickListener(v -> {
                for (String link : courseResource.getResourceLinks()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return courseIds.size();
    }

    // Filter method to search by course ID
    public void filter(String query) {
        courseIds.clear();
        if (query.isEmpty()) {
            courseIds.addAll(courseResources.keySet()); // Show all items if query is empty
        } else {
            for (String courseId : courseResources.keySet()) {
                if (courseId.toLowerCase().contains(query.toLowerCase())) {
                    courseIds.add(courseId); // Add matching course IDs
                }
            }
        }
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseId, courseActivities, courseOutlines, courseStructures, resourceLinks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseId = itemView.findViewById(R.id.courseId);
            courseActivities = itemView.findViewById(R.id.courseActivities);
            courseOutlines = itemView.findViewById(R.id.courseOutlines);
            courseStructures = itemView.findViewById(R.id.courseStructures);
            resourceLinks = itemView.findViewById(R.id.resourceLinks);
        }
    }
}