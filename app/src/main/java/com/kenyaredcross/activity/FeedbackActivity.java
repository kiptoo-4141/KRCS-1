package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackText;
    private Spinner recipientSpinner;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recipientList;
    private HashMap<String, String> userMap;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference usersRef;

    private String activityName;
    private String selectedRecipientEmail = "";

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

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Retrieve activity name from intent
        activityName = getIntent().getStringExtra("activityName");
        if (activityName == null) {
            Toast.makeText(this, "Activity name is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        feedbackText = findViewById(R.id.feedbackText);
        recipientSpinner = findViewById(R.id.recipientSpinner);

        // Initialize lists
        recipientList = new ArrayList<>();
        userMap = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, recipientList);
        recipientSpinner.setAdapter(adapter);

        // Load recipient list from Firebase
        loadRecipients();

        // Set listener for Spinner
        recipientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRecipient = recipientList.get(position);
                selectedRecipientEmail = userMap.get(selectedRecipient);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRecipientEmail = "";
            }
        });

        // Button Click Listener
        findViewById(R.id.sendfeedbackBtn).setOnClickListener(v -> sendFeedback());
    }

    private void loadRecipients() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                recipientList.clear();
                userMap.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String role = userSnapshot.child("role").getValue(String.class);
                    String username = userSnapshot.child("username").getValue(String.class);

                    if (email != null && role != null && username != null) {
                        if (role.equals("Service Manager") || role.equals("Trainer") || role.equals("Coordinator") ||
                                role.equals("Finance Manager") || role.equals("Inventory Manager") || role.equals("Supplier")) {
                            String displayText = username + " (" + role + ")";
                            recipientList.add(displayText);
                            userMap.put(displayText, email.replace(".", "_")); // Store email as Firebase key format
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FeedbackActivity.this, "Failed to load recipients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendFeedback() {
        String feedback = feedbackText.getText().toString().trim();
        if (feedback.isEmpty()) {
            feedbackText.setError("Feedback cannot be empty");
            return;
        }

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRecipientEmail.isEmpty()) {
            Toast.makeText(this, "Select a recipient", Toast.LENGTH_SHORT).show();
            return;
        }

        // Database reference
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback").child(selectedRecipientEmail);

        // Generate unique feedback_id
        String feedback_id = feedbackRef.push().getKey();
        if (feedback_id == null) {
            Toast.makeText(this, "Error generating feedback ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create feedback object
        HashMap<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("feedback_id", feedback_id);
        feedbackData.put("role", getUserRole());
        feedbackData.put("username", user.getDisplayName() != null ? user.getDisplayName() : "Unknown User");
        feedbackData.put("email", user.getEmail() != null ? user.getEmail() : "No Email");
        feedbackData.put("activity", activityName);
        feedbackData.put("feedback", feedback);
        feedbackData.put("dateSent", getCurrentDate());
        feedbackData.put("timeSent", getCurrentTime());
        feedbackData.put("status", "read");
        feedbackData.put("recipientEmail", selectedRecipientEmail);

        // Save feedback under recipient's email node
        feedbackRef.child(feedback_id).setValue(feedbackData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FeedbackActivity.this, "Feedback sent successfully!", Toast.LENGTH_SHORT).show();
                        feedbackText.setText("");
                    } else {
                        Toast.makeText(FeedbackActivity.this, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private String getUserRole() {
        // Placeholder logic - Update to retrieve actual user role
        return "User Role";
    }
}
