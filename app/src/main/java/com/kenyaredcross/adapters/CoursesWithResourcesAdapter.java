package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.EnrolledCourseModel;

import java.util.List;

public class CoursesWithResourcesAdapter extends FirebaseRecyclerAdapter<EnrolledCourseModel, CoursesWithResourcesAdapter.CourseWithResourcesViewHolder> {

    public CoursesWithResourcesAdapter(@NonNull FirebaseRecyclerOptions<EnrolledCourseModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CourseWithResourcesViewHolder holder, int position, @NonNull EnrolledCourseModel model) {
        holder.courseTitle.setText(model.getTitle());
        holder.courseDescription.setText(model.getDescription());

        // Get the course ID from the Firebase reference key
        String courseId = getRef(position).getKey();

        if (courseId != null) {
            // Load resources for the course
            DatabaseReference resourcesRef = FirebaseDatabase.getInstance().getReference("CoursesResources").child(courseId);
            resourcesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    StringBuilder resourcesBuilder = new StringBuilder();

                    if (snapshot.exists()) {
                        // Parse courseOutlines (array)
                        if (snapshot.child("courseOutlines").exists()) {
                            resourcesBuilder.append("Outlines:\n");
                            List<String> outlines = (List<String>) snapshot.child("courseOutlines").getValue();
                            if (outlines != null) {
                                for (String outline : outlines) {
                                    resourcesBuilder.append("- ").append(outline).append("\n");
                                }
                            }
                        }

                        // Parse courseActivities (array)
                        if (snapshot.child("courseActivities").exists()) {
                            resourcesBuilder.append("\nActivities:\n");
                            List<String> activities = (List<String>) snapshot.child("courseActivities").getValue();
                            if (activities != null) {
                                for (String activity : activities) {
                                    resourcesBuilder.append("- ").append(activity).append("\n");
                                }
                            }
                        }

                        // Parse courseStructures (array)
                        if (snapshot.child("courseStructures").exists()) {
                            resourcesBuilder.append("\nStructures:\n");
                            List<String> structures = (List<String>) snapshot.child("courseStructures").getValue();
                            if (structures != null) {
                                for (String structure : structures) {
                                    resourcesBuilder.append("- ").append(structure).append("\n");
                                }
                            }
                        }

                        // Parse resourceLinks (array)
                        if (snapshot.child("resourceLinks").exists()) {
                            resourcesBuilder.append("\nLinks:\n");
                            List<String> links = (List<String>) snapshot.child("resourceLinks").getValue();
                            if (links != null) {
                                for (String link : links) {
                                    resourcesBuilder.append("- ").append(link).append("\n");
                                }
                            }
                        }

                        // Set the text or show a message if no resources found
                        if (resourcesBuilder.length() > 0) {
                            holder.resourcesTextView.setText(resourcesBuilder.toString());
                        } else {
                            holder.resourcesTextView.setText("No resources found for this course");
                        }
                    } else {
                        holder.resourcesTextView.setText("No resources found for this course");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    holder.resourcesTextView.setText("Failed to load resources: " + error.getMessage());
                }
            });
        } else {
            holder.resourcesTextView.setText("Course ID not found");
        }
    }

    @NonNull
    @Override
    public CourseWithResourcesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_with_resources2, parent, false);
        return new CourseWithResourcesViewHolder(view);
    }

    static class CourseWithResourcesViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, courseDescription, resourcesTextView;

        public CourseWithResourcesViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            resourcesTextView = itemView.findViewById(R.id.resourcesTextView);
        }
    }
}