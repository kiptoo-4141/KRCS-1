package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Course;
import com.kenyaredcross.viewholder.CourseViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyTeachingCoursesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference coursesRef;
    private FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter;
    private SearchView searchView;
    private final String loggedInTrainerEmail = "trainer3@gmail.com"; // Replace with dynamically fetched email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_teaching_courses);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        coursesRef = FirebaseDatabase.getInstance().getReference("AssignedCourses");

        loadCourses("");

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadCourses(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadCourses(newText);
                return true;
            }
        });
    }

    private void loadCourses(String query) {
        HashSet<String> displayedCourses = new HashSet<>();

        FirebaseRecyclerOptions<Course> options = new FirebaseRecyclerOptions.Builder<Course>()
                .setQuery(coursesRef.orderByChild("trainerEmail").equalTo(loggedInTrainerEmail), Course.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(options) {
            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
                return new CourseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CourseViewHolder holder, int position, @NonNull Course model) {
                if (!displayedCourses.contains(model.getCourseId())) {
                    if (query.isEmpty() || model.getCourseTitle().toLowerCase().contains(query.toLowerCase())) {
                        holder.courseTitle.setText(model.getCourseTitle());
                        holder.courseDesc.setText(model.getCourseDescription());

                        holder.itemView.setOnClickListener(v -> openCoursePopup(model));
                        displayedCourses.add(model.getCourseId());
                    }
                }
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void openCoursePopup(Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_course_resources, null);

        EditText courseOutline = view.findViewById(R.id.courseOutline);
        EditText courseActivities = view.findViewById(R.id.courseActivities);
        EditText courseStructure = view.findViewById(R.id.courseStructure);
        EditText resourceLinks = view.findViewById(R.id.resourceLinks);

        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            addCourseResources(course.getCourseId(), courseOutline.getText().toString(),
                    courseActivities.getText().toString(), courseStructure.getText().toString(),
                    resourceLinks.getText().toString());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addCourseResources(String courseId, String outline, String activities, String structure, String links) {
        DatabaseReference resourcesRef = FirebaseDatabase.getInstance().getReference("CoursesResources").child(courseId);

        if (!outline.isEmpty()) {
            resourcesRef.child("courseOutlines").push().setValue(outline);
        }
        if (!activities.isEmpty()) {
            resourcesRef.child("courseActivities").push().setValue(activities);
        }
        if (!structure.isEmpty()) {
            resourcesRef.child("courseStructures").push().setValue(structure);
        }
        if (!links.isEmpty()) {
            resourcesRef.child("resourceLinks").push().setValue(links);
        }

        Toast.makeText(this, "Resources Added", Toast.LENGTH_SHORT).show();
    }
}
