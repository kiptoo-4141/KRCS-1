package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackText;
    private String activityName;

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Retrieve the activity name from the intent (auto-populated)
        activityName = getIntent().getStringExtra("activityName");
        if (activityName == null) {
            Toast.makeText(this, "Activity name is missing", Toast.LENGTH_SHORT).show();
            finish(); // Exit the activity if the activity name is not provided
            return;
        }

        feedbackText = findViewById(R.id.feedbackText);
        findViewById(R.id.sendfeedbackBtn).setOnClickListener(v -> sendFeedback());
    }

    private void sendFeedback() {
        String feedback = feedbackText.getText().toString().trim();
        if (feedback.isEmpty()) {
            feedbackText.setError("Feedback cannot be empty");
            return;
        }

        // Check if user is logged in
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a reference to the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("feedback");

        // Create a feedback object
        HashMap<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("role", getUserRole()); // Get user role
        feedbackData.put("username", user.getDisplayName() != null ? user.getDisplayName() : "Unknown User"); // Get username from Firebase
        feedbackData.put("email", user.getEmail() != null ? user.getEmail() : "No Email"); // Get email from Firebase
        feedbackData.put("activity", activityName); // Store the activity name
        feedbackData.put("feedback", feedback); // Store the feedback
        feedbackData.put("dateSent", getCurrentDate());
        feedbackData.put("timeSent", getCurrentTime());
        feedbackData.put("status", "read"); // Default status

        // Push the feedback data to the database under the activity name
        databaseReference.child(activityName).push().setValue(feedbackData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FeedbackActivity.this, "Feedback sent successfully!", Toast.LENGTH_SHORT).show();
                        feedbackText.setText(""); // Clear the feedback input
                    } else {
                        Toast.makeText(FeedbackActivity.this, "Failed to send feedback. Try again.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private String getUserRole() {
        // Logic to retrieve user role based on your application logic
        // This is a placeholder. Replace with actual role retrieval logic.
        return "User Role"; // Replace this with the actual logic to get the user's role
    }
}
