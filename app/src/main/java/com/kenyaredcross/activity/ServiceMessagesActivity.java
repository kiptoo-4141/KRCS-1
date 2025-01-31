package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.ContactUsMessage;
import com.kenyaredcross.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ServiceMessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<ContactUsMessage> messagesList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private EditText replyEditText;
    private Button sendReplyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_messages);

        // Set up layout
        recyclerView = findViewById(R.id.messagesRecyclerView);
        replyEditText = findViewById(R.id.replyEditText);
        sendReplyButton = findViewById(R.id.sendReplyButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messagesList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("ContactUsMessages");

        // Fetch messages from Firebase
        fetchMessages();

        // Set up button click to send reply
        sendReplyButton.setOnClickListener(v -> sendReply());
    }

    private void fetchMessages() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ContactUsMessage message = snapshot.getValue(ContactUsMessage.class);
                    if (message != null && "unread".equals(message.getStatus())) {
                        messagesList.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ServiceMessagesActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendReply() {
        String reply = replyEditText.getText().toString().trim();
        if (!reply.isEmpty()) {
            // Assuming the message being replied to is the first unread message in the list
            ContactUsMessage message = messagesList.get(0); // Modify if replying to specific message
            String messageId = message.getUserId(); // Use message ID to update in Firebase
            DatabaseReference messageRef = databaseReference.child(messageId);

            // Update message status to "replied" and save the reply
            messageRef.child("status").setValue("replied");
            messageRef.child("reply").setValue(reply);  // Save the reply (you can add a new field for reply)

            Toast.makeText(this, "Reply sent", Toast.LENGTH_SHORT).show();
            replyEditText.setText("");  // Clear the reply field
        } else {
            Toast.makeText(this, "Please enter a reply", Toast.LENGTH_SHORT).show();
        }
    }
}
