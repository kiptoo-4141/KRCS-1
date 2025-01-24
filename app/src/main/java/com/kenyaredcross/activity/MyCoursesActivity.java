package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.EnrolledCourseAdapter;
import com.kenyaredcross.domain_model.EnrolledCourseModel;

public class MyCoursesActivity extends AppCompatActivity {

    TextView feedback;
    RecyclerView mycourses;
    EnrolledCourseAdapter enrolledCourseAdapter;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Ensure user is authenticated
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                // Replace '.' in email with '_' for Firebase node key
                String userEmailKey = userEmail.replace(".", "_");

                // Set up Firebase reference to enrollments of the user
                DatabaseReference enrollmentsRef = FirebaseDatabase.getInstance().getReference()
                        .child("Enrollments")
                        .child(userEmailKey);

                // Modify the query to only fetch courses with 'approved' status
                Query approvedCoursesQuery = enrollmentsRef.orderByChild("status").equalTo("approved");

                // Set up FirebaseRecyclerOptions with the query for approved courses
                FirebaseRecyclerOptions<EnrolledCourseModel> options =
                        new FirebaseRecyclerOptions.Builder<EnrolledCourseModel>()
                                .setQuery(approvedCoursesQuery, EnrolledCourseModel.class) // use query here
                                .build();

                // Set up RecyclerView
                mycourses = findViewById(R.id.myCoursesView);
                mycourses.setLayoutManager(new LinearLayoutManager(this));

                // Set up adapter
                enrolledCourseAdapter = new EnrolledCourseAdapter(options);
                mycourses.setAdapter(enrolledCourseAdapter);
            }
        }

        // Apply window insets to avoid overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        feedback = findViewById(R.id.feedbackLink);
        feedback.setOnClickListener(view -> {
            Intent intent = new Intent(MyCoursesActivity.this, FeedbackActivity.class);
            intent.putExtra("activityName", "MyCoursesActivity");
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (enrolledCourseAdapter != null) {
            enrolledCourseAdapter.startListening();  // Start listening for Firebase data
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (enrolledCourseAdapter != null) {
            enrolledCourseAdapter.stopListening();  // Stop listening when activity is stopped
        }
    }
}
