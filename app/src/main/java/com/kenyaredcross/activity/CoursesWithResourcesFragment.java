package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Course;
import com.kenyaredcross.viewholder.CourseWithResourcesViewHolder;

public class CoursesWithResourcesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference coursesRef;
    private FirebaseRecyclerAdapter<Course, CourseWithResourcesViewHolder> adapter;
    private final String loggedInTrainerEmail;

    public CoursesWithResourcesFragment(String loggedInTrainerEmail) {
        this.loggedInTrainerEmail = loggedInTrainerEmail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses_with_resources, container, false);

        recyclerView = view.findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        coursesRef = FirebaseDatabase.getInstance().getReference("AssignedCourses");
        loadCoursesWithResources();

        return view;
    }

    private void loadCoursesWithResources() {
        FirebaseRecyclerOptions<Course> options = new FirebaseRecyclerOptions.Builder<Course>()
                .setQuery(coursesRef.orderByChild("trainerEmail").equalTo(loggedInTrainerEmail), Course.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Course, CourseWithResourcesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CourseWithResourcesViewHolder holder, int position, @NonNull Course model) {
                // Bind course data to the views
                holder.courseTitle.setText(model.getCourseTitle());
                holder.courseDesc.setText(model.getCourseDescription());

                // Load resources for the course
                DatabaseReference resourcesRef = FirebaseDatabase.getInstance().getReference("CoursesResources").child(model.getCourseId());
                resourcesRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        holder.setResources(task.getResult());
                    } else {
                        Toast.makeText(getContext(), "Failed to load resources", Toast.LENGTH_SHORT).show();
                    }
                });

                // Edit resources
                holder.editButton.setOnClickListener(v -> {
                    openEditResourcesDialog(model.getCourseId());
                });
            }

            @NonNull
            @Override
            public CourseWithResourcesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_with_resources, parent, false);
                return new CourseWithResourcesViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void openEditResourcesDialog(String courseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_resources, null);
        builder.setView(dialogView);

        EditText outlineEditText = dialogView.findViewById(R.id.outlineEditText);
        EditText activitiesEditText = dialogView.findViewById(R.id.activitiesEditText);
        EditText structureEditText = dialogView.findViewById(R.id.structureEditText);
        EditText linksEditText = dialogView.findViewById(R.id.linksEditText);

        // Load existing resources into the dialog
        DatabaseReference resourcesRef = FirebaseDatabase.getInstance().getReference("CoursesResources").child(courseId);
        resourcesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    // Load courseOutlines
                    if (snapshot.child("courseOutlines").exists()) {
                        StringBuilder outlinesBuilder = new StringBuilder();
                        for (DataSnapshot outline : snapshot.child("courseOutlines").getChildren()) {
                            outlinesBuilder.append(outline.getValue(String.class)).append("\n");
                        }
                        outlineEditText.setText(outlinesBuilder.toString().trim());
                    }

                    // Load courseActivities
                    if (snapshot.child("courseActivities").exists()) {
                        StringBuilder activitiesBuilder = new StringBuilder();
                        for (DataSnapshot activity : snapshot.child("courseActivities").getChildren()) {
                            activitiesBuilder.append(activity.getValue(String.class)).append("\n");
                        }
                        activitiesEditText.setText(activitiesBuilder.toString().trim());
                    }

                    // Load courseStructures
                    if (snapshot.child("courseStructures").exists()) {
                        StringBuilder structuresBuilder = new StringBuilder();
                        for (DataSnapshot structure : snapshot.child("courseStructures").getChildren()) {
                            structuresBuilder.append(structure.getValue(String.class)).append("\n");
                        }
                        structureEditText.setText(structuresBuilder.toString().trim());
                    }

                    // Load resourceLinks
                    if (snapshot.child("resourceLinks").exists()) {
                        StringBuilder linksBuilder = new StringBuilder();
                        for (DataSnapshot link : snapshot.child("resourceLinks").getChildren()) {
                            linksBuilder.append(link.getValue(String.class)).append("\n");
                        }
                        linksEditText.setText(linksBuilder.toString().trim());
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to load resources", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            String outline = outlineEditText.getText().toString().trim();
            String activities = activitiesEditText.getText().toString().trim();
            String structure = structureEditText.getText().toString().trim();
            String links = linksEditText.getText().toString().trim();

            saveResources(courseId, outline, activities, structure, links);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void saveResources(String courseId, String outline, String activities, String structure, String links) {
        DatabaseReference resourcesRef = FirebaseDatabase.getInstance().getReference("CoursesResources").child(courseId);

        // Save courseOutlines
        if (!outline.isEmpty()) {
            String[] outlineLines = outline.split("\n");
            for (int i = 0; i < outlineLines.length; i++) {
                resourcesRef.child("courseOutlines").child(String.valueOf(i)).setValue(outlineLines[i]);
            }
        }

        // Save courseActivities
        if (!activities.isEmpty()) {
            String[] activityLines = activities.split("\n");
            for (int i = 0; i < activityLines.length; i++) {
                resourcesRef.child("courseActivities").child(String.valueOf(i)).setValue(activityLines[i]);
            }
        }

        // Save courseStructures
        if (!structure.isEmpty()) {
            String[] structureLines = structure.split("\n");
            for (int i = 0; i < structureLines.length; i++) {
                resourcesRef.child("courseStructures").child(String.valueOf(i)).setValue(structureLines[i]);
            }
        }

        // Save resourceLinks
        if (!links.isEmpty()) {
            String[] linkLines = links.split("\n");
            for (int i = 0; i < linkLines.length; i++) {
                resourcesRef.child("resourceLinks").child(String.valueOf(i)).setValue(linkLines[i]);
            }
        }

        Toast.makeText(getContext(), "Resources saved successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}