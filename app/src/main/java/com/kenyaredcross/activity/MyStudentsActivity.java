package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
    private FirebaseRecyclerAdapter<AssignedCourseModel, AssignedCourseViewHolder> assignedCoursesAdapter;
    private String trainerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_students);

        assignedCoursesRecyclerView = findViewById(R.id.assignedCoursesRecyclerView);
        assignedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            trainerEmail = currentUser.getEmail();
            fetchAssignedCourses();
        } else {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAssignedCourses() {
        if (trainerEmail == null) return;

        DatabaseReference assignedCoursesRef = FirebaseDatabase.getInstance().getReference().child("AssignedCourses");
        Query query = assignedCoursesRef.orderByChild("trainerEmail").equalTo(trainerEmail);

        FirebaseRecyclerOptions<AssignedCourseModel> options = new FirebaseRecyclerOptions.Builder<AssignedCourseModel>()
                .setQuery(query, AssignedCourseModel.class)
                .build();

        assignedCoursesAdapter = new FirebaseRecyclerAdapter<AssignedCourseModel, AssignedCourseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AssignedCourseViewHolder holder, int position, @NonNull AssignedCourseModel model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public AssignedCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_with_students, parent, false);
                return new AssignedCourseViewHolder(view);
            }
        };

        assignedCoursesRecyclerView.setAdapter(assignedCoursesAdapter);
        assignedCoursesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (assignedCoursesAdapter != null) {
            assignedCoursesAdapter.stopListening();
        }
    }

    public static class AssignedCourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView courseTitleTextView;
        private final RecyclerView enrolledUsersRecyclerView;
        private FirebaseRecyclerAdapter<EnrollmentModel, EnrollmentViewHolder> enrolledUsersAdapter;

        public AssignedCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitleTextView = itemView.findViewById(R.id.courseTitleTextView);
            enrolledUsersRecyclerView = itemView.findViewById(R.id.enrolledUsersRecyclerView);
            enrolledUsersRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

        public void bind(AssignedCourseModel model) {
            courseTitleTextView.setText(model.getCourseTitle());
            fetchEnrolledUsers(model.getCourseTitle());
        }

        private void fetchEnrolledUsers(String courseTitle) {
            DatabaseReference enrollmentsRef = FirebaseDatabase.getInstance().getReference().child("Enrollments");
            Query query = enrollmentsRef.orderByChild("title").equalTo(courseTitle);

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
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
                    return new EnrollmentViewHolder(view);
                }
            };

            enrolledUsersRecyclerView.setAdapter(enrolledUsersAdapter);
            enrolledUsersAdapter.startListening();
        }
    }

    public static class EnrollmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView studentNameTextView;
        private final Button passButton, failButton;

        public EnrollmentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.studentNameTextView);
            passButton = itemView.findViewById(R.id.passButton);
            failButton = itemView.findViewById(R.id.failButton);
        }

        public void bind(EnrollmentModel model) {
            studentNameTextView.setText(model.getUsername());
            passButton.setOnClickListener(v -> updateStudentStatus(model.getEmail(), model.getTitle(), "Passed"));
            failButton.setOnClickListener(v -> updateStudentStatus(model.getEmail(), model.getTitle(), "Failed"));
        }

        private void updateStudentStatus(String userEmail, String courseTitle, String status) {
            String formattedEmail = userEmail.replace(".", "_");
            DatabaseReference enrollmentRef = FirebaseDatabase.getInstance().getReference()
                    .child("Enrollments").child(formattedEmail).child(courseTitle).child("status");
            enrollmentRef.setValue(status).addOnSuccessListener(aVoid ->
                    Toast.makeText(itemView.getContext(), userEmail + " marked as " + status, Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(itemView.getContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
