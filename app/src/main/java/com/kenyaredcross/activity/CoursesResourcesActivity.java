package com.kenyaredcross.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.CourseAdapter;
import com.kenyaredcross.domain_model.Courses;

import java.util.ArrayList;
import java.util.List;

public class CoursesResourcesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Courses> courseList;
    private DatabaseReference databaseReference;
    private String userEmailFormatted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses_resources);

        recyclerView = findViewById(R.id.recyclerViewCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(courseAdapter);

        // Get logged-in user email and format it
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail != null) {
            userEmailFormatted = userEmail.replace(".", "_");  // Firebase key-safe format
            fetchUserCourses();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserCourses() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Enrollments").child(userEmailFormatted);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                        String courseId = courseSnapshot.getKey();
                        fetchCourseDetails(courseId);
                    }
                } else {
                    Toast.makeText(CoursesResourcesActivity.this, "No enrolled courses found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error fetching enrolled courses", error.toException());
            }
        });
    }

    private void fetchCourseDetails(String courseId) {
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("Courses").child("course" + courseId);
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Courses course = snapshot.getValue(Courses.class);
                    if (course != null) {
                        fetchCourseResources(course);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error fetching course details", error.toException());
            }
        });
    }

    private void fetchCourseResources(Courses course) {
        DatabaseReference resourcesRef = FirebaseDatabase.getInstance().getReference("CoursesResources").child(course.getId());
        resourcesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> resources = new ArrayList<>();
                    for (DataSnapshot resourceSnapshot : snapshot.getChildren()) {
                        resources.add(resourceSnapshot.getValue(String.class));
                    }
                    course.setResources(resources);
                }
                courseList.add(course);
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error fetching course resources", error.toException());
            }
        });
    }
}
