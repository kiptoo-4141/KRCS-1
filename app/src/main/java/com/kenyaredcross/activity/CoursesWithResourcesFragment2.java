package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.CoursesWithResourcesAdapter;
import com.kenyaredcross.domain_model.EnrolledCourseModel;

public class CoursesWithResourcesFragment2 extends Fragment {

    private RecyclerView recyclerView;
    private CoursesWithResourcesAdapter adapter;
    private final String userEmailKey;

    public CoursesWithResourcesFragment2(String userEmailKey) {
        this.userEmailKey = userEmailKey;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses_with_resources2, container, false);

        recyclerView = view.findViewById(R.id.resourcesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Validate userEmailKey
        if (userEmailKey == null || userEmailKey.isEmpty()) {
            Toast.makeText(getContext(), "User email not available", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Set up Firebase reference to enrollments of the user
        DatabaseReference enrollmentsRef = FirebaseDatabase.getInstance().getReference()
                .child("Enrollments")
                .child(userEmailKey.replace(".", "_")); // Replace dots with underscores for Firebase key compatibility

        // Query to fetch only approved courses
        Query approvedCoursesQuery = enrollmentsRef.orderByChild("status").equalTo("approved");

        // Set up FirebaseRecyclerOptions for the adapter
        FirebaseRecyclerOptions<EnrolledCourseModel> options =
                new FirebaseRecyclerOptions.Builder<EnrolledCourseModel>()
                        .setQuery(approvedCoursesQuery, EnrolledCourseModel.class)
                        .build();

        // Initialize and set the adapter
        adapter = new CoursesWithResourcesAdapter(options);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening(); // Start listening for Firebase data
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening(); // Stop listening when the fragment is stopped
        }
    }
}