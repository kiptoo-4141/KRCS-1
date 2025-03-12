package com.kenyaredcross.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.kenyaredcross.R;

public class CourseWithResourcesViewHolder extends RecyclerView.ViewHolder {

    public TextView courseTitle, courseDesc, resourcesTextView;
    public Button editButton;

    public CourseWithResourcesViewHolder(@NonNull View itemView) {
        super(itemView);

        courseTitle = itemView.findViewById(R.id.courseTitle);
        courseDesc = itemView.findViewById(R.id.courseDesc);
        resourcesTextView = itemView.findViewById(R.id.resourcesTextView);
        editButton = itemView.findViewById(R.id.editButton);
    }

    public void setResources(DataSnapshot resourcesSnapshot) {
        StringBuilder resourcesBuilder = new StringBuilder();

        // Parse courseOutlines
        if (resourcesSnapshot.child("courseOutlines").exists()) {
            resourcesBuilder.append("Outlines:\n");
            for (DataSnapshot outline : resourcesSnapshot.child("courseOutlines").getChildren()) {
                resourcesBuilder.append("- ").append(outline.getValue(String.class)).append("\n");
            }
        }

        // Parse courseActivities
        if (resourcesSnapshot.child("courseActivities").exists()) {
            resourcesBuilder.append("Activities:\n");
            for (DataSnapshot activity : resourcesSnapshot.child("courseActivities").getChildren()) {
                resourcesBuilder.append("- ").append(activity.getValue(String.class)).append("\n");
            }
        }

        // Parse courseStructures
        if (resourcesSnapshot.child("courseStructures").exists()) {
            resourcesBuilder.append("Structures:\n");
            for (DataSnapshot structure : resourcesSnapshot.child("courseStructures").getChildren()) {
                resourcesBuilder.append("- ").append(structure.getValue(String.class)).append("\n");
            }
        }

        // Parse resourceLinks
        if (resourcesSnapshot.child("resourceLinks").exists()) {
            resourcesBuilder.append("Links:\n");
            for (DataSnapshot link : resourcesSnapshot.child("resourceLinks").getChildren()) {
                resourcesBuilder.append("- ").append(link.getValue(String.class)).append("\n");
            }
        }

        if (resourcesTextView != null) {
            resourcesTextView.setText(resourcesBuilder.toString());
        }
    }
}