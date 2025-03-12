package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.EnrolledCourseAdapter;
import com.kenyaredcross.domain_model.EnrolledCourseModel;

public class EnrolledCoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    private EnrolledCourseAdapter enrolledCourseAdapter;
    private final String userEmailKey;

    public EnrolledCoursesFragment(String userEmailKey) {
        this.userEmailKey = userEmailKey;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_courses, container, false);

        recyclerView = view.findViewById(R.id.myCoursesView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up Firebase reference to enrollments of the user
        DatabaseReference enrollmentsRef = FirebaseDatabase.getInstance().getReference()
                .child("Enrollments")
                .child(userEmailKey);

        // Modify the query to only fetch courses with 'approved' status
        Query approvedCoursesQuery = enrollmentsRef.orderByChild("status").equalTo("approved");

        // Set up FirebaseRecyclerOptions with the query for approved courses
        FirebaseRecyclerOptions<EnrolledCourseModel> options =
                new FirebaseRecyclerOptions.Builder<EnrolledCourseModel>()
                        .setQuery(approvedCoursesQuery, EnrolledCourseModel.class)
                        .build();

        // Set up adapter
        enrolledCourseAdapter = new EnrolledCourseAdapter(options);
        recyclerView.setAdapter(enrolledCourseAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (enrolledCourseAdapter != null) {
            enrolledCourseAdapter.startListening();  // Start listening for Firebase data
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (enrolledCourseAdapter != null) {
            enrolledCourseAdapter.stopListening();  // Stop listening when fragment is stopped
        }
    }
}