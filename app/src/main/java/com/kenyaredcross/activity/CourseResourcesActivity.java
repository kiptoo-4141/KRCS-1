package com.kenyaredcross.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.SearchView;
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
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_resources);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchView);
        setupSearchView();

        fetchAllCourseResources();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the RecyclerView based on the search query
                adapter.filter(newText);
                return true;
            }
        });
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