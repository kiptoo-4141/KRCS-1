package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.CourseResource;
import java.util.List;

public class CourseResourceAdapter extends RecyclerView.Adapter<CourseResourceAdapter.ViewHolder> {
    private List<CourseResource> courseResourceList;

    public CourseResourceAdapter(List<CourseResource> courseResourceList) {
        this.courseResourceList = courseResourceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseResource courseResource = courseResourceList.get(position);
        holder.courseId.setText("Course ID: " + courseResource.getCourseId());
        holder.activities.setText("Activities: " + courseResource.getCourseActivities().values());
        holder.outlines.setText("Outlines: " + courseResource.getCourseOutlines().values());
        holder.structures.setText("Structures: " + courseResource.getCourseStructures().values());
        holder.resourceLinks.setText("Resources: " + courseResource.getResourceLinks().values());
    }

    @Override
    public int getItemCount() {
        return courseResourceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseId, activities, outlines, structures, resourceLinks;

        public ViewHolder(View itemView) {
            super(itemView);
            courseId = itemView.findViewById(R.id.courseId);
            activities = itemView.findViewById(R.id.activities);
            outlines = itemView.findViewById(R.id.outlines);
            structures = itemView.findViewById(R.id.structures);
            resourceLinks = itemView.findViewById(R.id.resourceLinks);
        }
    }
}
