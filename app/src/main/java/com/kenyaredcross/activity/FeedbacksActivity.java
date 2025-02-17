package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.FeedbacksModel;

import java.util.UUID;

public class FeedbacksActivity extends AppCompatActivity {

    private EditText messageInput;
    private Spinner userSpinner;
    private Button sendFeedbackButton;
    private DatabaseReference feedbackRef;
    private final String senderId = "currentUserEmail"; // Replace with actual sender

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        messageInput = findViewById(R.id.messageInput);
        userSpinner = findViewById(R.id.userSpinner);
        sendFeedbackButton = findViewById(R.id.sendFeedbackButton);

        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedbacks");

        sendFeedbackButton.setOnClickListener(v -> sendFeedback());
    }

    private void sendFeedback() {
        String receiverId = userSpinner.getSelectedItem().toString();
        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        FeedbacksModel feedback = new FeedbacksModel(feedbackId, senderId, receiverId, message, timestamp);
        feedbackRef.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Feedback sent!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to send feedback", Toast.LENGTH_SHORT).show());
    }
}
