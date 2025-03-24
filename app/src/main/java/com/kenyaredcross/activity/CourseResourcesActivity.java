package com.kenyaredcross.activity;

import android.os.Bundle;
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
import com.kenyaredcross.adapters.CourseResourceAdapter2;
import com.kenyaredcross.domain_model.CourseResource2;
import java.util.HashMap;
import java.util.Map;

public class CourseResourcesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseResourceAdapter2 adapter;
    private Map<String, CourseResource2> courseResources = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_resources);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchAllCourseResources();
    }

    private void fetchAllCourseResources() {
        DatabaseReference resourcesRef = FirebaseDatabase.getInstance().getReference("CoursesResources");
        resourcesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String courseId = courseSnapshot.getKey();
                    CourseResource2 courseResource = courseSnapshot.getValue(CourseResource2.class);
                    courseResources.put(courseId, courseResource);
                }
                adapter = new CourseResourceAdapter2(courseResources);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}