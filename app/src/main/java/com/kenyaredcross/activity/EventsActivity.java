package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.EventsAdapter;
import com.kenyaredcross.domain_model.EventModel;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView eventsView;
    private EventsAdapter eventAdapter;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventsView = findViewById(R.id.eventsView);
        eventsView.setLayoutManager(new LinearLayoutManager(this));

        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");

        FirebaseRecyclerOptions<EventModel> options =
                new FirebaseRecyclerOptions.Builder<EventModel>()
                        .setQuery(eventsRef, EventModel.class)
                        .build();

        eventAdapter = new EventsAdapter(options);
        eventsView.setAdapter(eventAdapter);

        TextView feedback = findViewById(R.id.feedbackLink);
        feedback.setOnClickListener(view -> {
            Intent intent = new Intent(EventsActivity.this, FeedbackActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (eventAdapter != null) {
            eventAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (eventAdapter != null) {
            eventAdapter.stopListening();
        }
    }
}
