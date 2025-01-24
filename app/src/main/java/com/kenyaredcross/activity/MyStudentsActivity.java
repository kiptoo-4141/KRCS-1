package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.AssignedCourseModel;
import com.kenyaredcross.domain_model.EnrollmentModel;

public class MyStudentsActivity extends AppCompatActivity {

    private RecyclerView assignedCoursesRecyclerView;
    private RecyclerView enrolledUsersRecyclerView;
    private FirebaseRecyclerAdapter<AssignedCourseModel, AssignedCourseViewHolder> assignedCoursesAdapter;
    private FirebaseRecyclerAdapter<EnrollmentModel, EnrollmentViewHolder> enrolledUsersAdapter;
    private String trainerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_students);

        assignedCoursesRecyclerView = findViewById(R.id.assignedCoursesRecyclerView);
        assignedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        enrolledUsersRecyclerView = findViewById(R.id.enrolledUsersRecyclerView);
        enrolledUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the currently logged-in user's email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            trainerEmail = currentUser.getEmail();
            fetchAssignedCourses();
        } else {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAssignedCourses() {
        if (trainerEmail == null) return; // Ensure trainerEmail is available

        DatabaseReference assignedCoursesRef = FirebaseDatabase.getInstance().getReference().child("AssignedCourses");
        Query query = assignedCoursesRef.orderByChild("trainerEmail").equalTo(trainerEmail);

        FirebaseRecyclerOptions<AssignedCourseModel> options = new FirebaseRecyclerOptions.Builder<AssignedCourseModel>()
                .setQuery(query, AssignedCourseModel.class)
                .build();

        assignedCoursesAdapter = new FirebaseRecyclerAdapter<AssignedCourseModel, AssignedCourseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AssignedCourseViewHolder holder, int position, @NonNull AssignedCourseModel model) {
                holder.bind(model);
                holder.itemView.setOnClickListener(view -> fetchEnrolledUsers(model.getCourseId()));
            }

            @NonNull
            @Override
            public AssignedCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
                return new AssignedCourseViewHolder(view);
            }
        };

        assignedCoursesRecyclerView.setAdapter(assignedCoursesAdapter);
        assignedCoursesAdapter.startListening();
    }

    private void fetchEnrolledUsers(String courseId) {
        DatabaseReference enrollmentsRef = FirebaseDatabase.getInstance().getReference().child("Enrollments");
        Query query = enrollmentsRef.orderByChild("courseId").equalTo(courseId);

        FirebaseRecyclerOptions<EnrollmentModel> options = new FirebaseRecyclerOptions.Builder<EnrollmentModel>()
                .setQuery(query, EnrollmentModel.class)
                .build();

        enrolledUsersAdapter = new FirebaseRecyclerAdapter<EnrollmentModel, EnrollmentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EnrollmentViewHolder holder, int position, @NonNull EnrollmentModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public EnrollmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enrollment, parent, false);
                return new EnrollmentViewHolder(view);
            }
        };

        enrolledUsersRecyclerView.setAdapter(enrolledUsersAdapter);
        enrolledUsersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (assignedCoursesAdapter != null) {
            assignedCoursesAdapter.stopListening();
        }
        if (enrolledUsersAdapter != null) {
            enrolledUsersAdapter.stopListening();
        }
    }
}
