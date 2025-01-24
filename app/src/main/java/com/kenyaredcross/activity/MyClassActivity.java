package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyClassActivity extends AppCompatActivity {

    private Spinner courseSpinner;
    private RecyclerView enrolledUsersRecyclerView;
    private EnrolledUsersAdapter enrolledUsersAdapter;
    private DatabaseReference assignedCoursesRef, enrollmentsRef;
    private final List<String> courseTitles = new ArrayList<>();
    private String selectedCourseId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        courseSpinner = findViewById(R.id.course_spinner);
        enrolledUsersRecyclerView = findViewById(R.id.recycler_view_enrolled_users);

        // Firebase references
        assignedCoursesRef = FirebaseDatabase.getInstance().getReference("AssignedCourses");
        enrollmentsRef = FirebaseDatabase.getInstance().getReference("Enrollments");

        // Setup RecyclerView
        enrolledUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enrolledUsersAdapter = new EnrolledUsersAdapter(new ArrayList<>());
        enrolledUsersRecyclerView.setAdapter(enrolledUsersAdapter);

        // Fetch courses assigned to the trainer
        fetchAssignedCourses();

        // Set listener for course selection
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected course ID
                selectedCourseId = courseTitles.get(position);
                fetchEnrolledUsers(selectedCourseId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void fetchAssignedCourses() {
        // Assuming that trainer's email is stored in Firebase Auth or passed in intent
        String trainerEmail = "trainer3@gmail.com";  // Replace with dynamic trainer email

        assignedCoursesRef.orderByChild("trainerEmail").equalTo(trainerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courseTitles.clear();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseTitle = courseSnapshot.child("courseTitle").getValue(String.class);
                    courseTitles.add(courseTitle);
                }

                ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(MyClassActivity.this, android.R.layout.simple_spinner_item, courseTitles);
                courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                courseSpinner.setAdapter(courseAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyClassActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEnrolledUsers(String courseId) {
        enrollmentsRef.orderByChild("courseId").equalTo(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<EnrolledUser> enrolledUsers = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String status = userSnapshot.child("status").getValue(String.class);
                    if ("approved".equals(status)) {
                        String username = userSnapshot.child("username").getValue(String.class);
                        String courseTitle = userSnapshot.child("title").getValue(String.class);
                        String courseDescription = userSnapshot.child("description").getValue(String.class);
                        String image = userSnapshot.child("image").getValue(String.class);
                        enrolledUsers.add(new EnrolledUser(username, courseTitle, courseDescription, image));
                    }
                }
                enrolledUsersAdapter.updateUsers(enrolledUsers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyClassActivity.this, "Failed to load enrolled users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Enrolled user model
    static class EnrolledUser {
        String username;
        String courseTitle;
        String courseDescription;
        String image;

        EnrolledUser(String username, String courseTitle, String courseDescription, String image) {
            this.username = username;
            this.courseTitle = courseTitle;
            this.courseDescription = courseDescription;
            this.image = image;
        }
    }

    // RecyclerView Adapter for enrolled users
    static class EnrolledUsersAdapter extends RecyclerView.Adapter<EnrolledUsersAdapter.UserViewHolder> {

        private final List<EnrolledUser> enrolledUsers;

        EnrolledUsersAdapter(List<EnrolledUser> enrolledUsers) {
            this.enrolledUsers = enrolledUsers;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enrolled_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            EnrolledUser user = enrolledUsers.get(position);
            holder.username.setText(user.username);
            holder.courseTitle.setText(user.courseTitle);
            holder.courseDescription.setText(user.courseDescription);
            // Set Image using Glide or Picasso
            holder.progressBar.setProgress(50);  // Update progress based on some logic

            holder.completeButton.setOnClickListener(v -> {
                // Update status to completed (this is just an example)
                Toast.makeText(v.getContext(), "Marked " + user.username + " as completed", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return enrolledUsers.size();
        }

        public void updateUsers(List<EnrolledUser> newUsers) {
            enrolledUsers.clear();
            enrolledUsers.addAll(newUsers);
            notifyDataSetChanged();
        }

        static class UserViewHolder extends RecyclerView.ViewHolder {

            TextView username, courseTitle, courseDescription;
            ProgressBar progressBar;
            Button completeButton;

            UserViewHolder(View itemView) {
                super(itemView);
                username = itemView.findViewById(R.id.username);
                courseTitle = itemView.findViewById(R.id.course_title);
                courseDescription = itemView.findViewById(R.id.course_description);
                progressBar = itemView.findViewById(R.id.progress_bar);
                completeButton = itemView.findViewById(R.id.complete_button);
            }
        }
    }
}
