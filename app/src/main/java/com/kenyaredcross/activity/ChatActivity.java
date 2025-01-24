package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kenyaredcross.R;

public class ChatActivity extends AppCompatActivity {

    private TextView recipientUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize the TextView to display the recipient's username
        recipientUsername = findViewById(R.id.recepientUsername);

        // Retrieve the username passed through the Intent
        Intent intent = getIntent();
        String recipient = intent.getStringExtra("recipientUsername");

        // Set the recipient's username in the TextView
        if (recipient != null) {
            recipientUsername.setText(recipient);
        }
    }
}
