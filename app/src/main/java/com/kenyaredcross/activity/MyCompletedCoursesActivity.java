package com.kenyaredcross.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.MyCompletedCoursesAdapter;
import com.kenyaredcross.domain_model.MyCompletedCoursesModel;

import java.util.ArrayList;
import java.util.List;

public class MyCompletedCoursesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyCompletedCoursesAdapter adapter;
    private List<MyCompletedCoursesModel> courseList;
    private DatabaseReference enrollmentsRef, coursesRef;
    private final String userEmail = "youth1@gmail_com"; // Get dynamically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_completed_courses);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseList = new ArrayList<>();
        adapter = new MyCompletedCoursesAdapter(this, courseList);
        recyclerView.setAdapter(adapter);

        enrollmentsRef = FirebaseDatabase.getInstance().getReference("Enrollments").child(userEmail);
        coursesRef = FirebaseDatabase.getInstance().getReference("Courses");

        fetchCompletedCourses();
    }

    private void fetchCompletedCourses() {
        enrollmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String status = courseSnapshot.child("status").getValue(String.class);
                    if (status != null && status.equals("completed")) {
                        String courseTitle = courseSnapshot.child("title").getValue(String.class);
                        fetchCourseDetails(courseTitle);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching enrollments", error.toException());
            }
        });
    }

    private void fetchCourseDetails(String title) {
        coursesRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    MyCompletedCoursesModel course = courseSnapshot.getValue(MyCompletedCoursesModel.class);
                    if (course != null) {
                        courseList.add(course);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching course details", error.toException());
            }
        });
    }
}
