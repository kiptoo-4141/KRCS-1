package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;

public class ContactUsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText contactFullName, contactPhoneNumber, contactEmail, contactMessage;
    private Button contactSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // Initialize Firebase Auth and Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Find Views by ID
        contactFullName = findViewById(R.id.contactFullName);
        contactPhoneNumber = findViewById(R.id.contactPhoneNumber);
        contactEmail = findViewById(R.id.contactEmail);
        contactMessage = findViewById(R.id.contactMessage);
        contactSendMessage = findViewById(R.id.contactSendMessage);

        // Check if user is logged in
        if (mAuth.getCurrentUser() != null) {
            // Hide the EditTexts for Full Name, Phone, and Email
            contactFullName.setVisibility(View.GONE);
            contactPhoneNumber.setVisibility(View.GONE);
            contactEmail.setVisibility(View.GONE);
        }

        // Handle the Send Message button click
        contactSendMessage.setOnClickListener(v -> {
            String message = contactMessage.getText().toString().trim();

            if (!message.isEmpty()) {
                // User is logged in, use their details
                if (mAuth.getCurrentUser() != null) {
                    String userId = mAuth.getCurrentUser().getUid();
                    String userEmail = mAuth.getCurrentUser().getEmail();
                    String userName = mAuth.getCurrentUser().getDisplayName(); // Or retrieve from database if not available

                    // Create a new message node in Firebase
                    String messageId = mDatabase.child("ContactUsMessages").push().getKey();
                    ContactUsMessage contactMessageData = new ContactUsMessage(userId, userName, userEmail, message, "unread");

                    mDatabase.child("ContactUsMessages").child(messageId).setValue(contactMessageData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Show a success message or clear the fields
                                } else {
                                    // Handle failure
                                }
                            });
                }
            }
        });
    }

    // ContactUsMessage model class
    public static class ContactUsMessage {
        public String userId, userName, userEmail, message, status;

        public ContactUsMessage(String userId, String userName, String userEmail, String message, String status) {
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.message = message;
            this.status = status;
        }
    }
}
