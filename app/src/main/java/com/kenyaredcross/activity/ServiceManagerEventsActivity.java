package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.EventAdapter;
import com.kenyaredcross.domain_model.EventModel2;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ServiceManagerEventsActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<EventModel2> eventList;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_manager_events);

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        Button btnAddEvent = findViewById(R.id.btnAddEvent);

        eventList = new ArrayList<>();
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");

        // Set up RecyclerView
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList, this);
        eventsRecyclerView.setAdapter(eventAdapter);

        // Load events from Firebase
        loadEvents();

        // Add new event
        btnAddEvent.setOnClickListener(v -> {
            // Open a dialog or new activity to add a new event
            showAddOrEditEventDialog(null);
        });
    }

    private void loadEvents() {
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    EventModel2 event = dataSnapshot.getValue(EventModel2.class);
                    if (event != null) {
                        event.setEvent_ID(dataSnapshot.getKey()); // Set the event ID
                        eventList.add(event);
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ServiceManagerEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditEvent(EventModel2 event) {
        // Open a dialog to edit the event
        showAddOrEditEventDialog(event);
    }

    @Override
    public void onDeleteEvent(EventModel2 event) {
        // Delete the event from Firebase
        eventsRef.child(event.getEvent_ID()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show());
    }

    private void showAddOrEditEventDialog(EventModel2 event) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_event, null);
        dialogBuilder.setView(dialogView);

        EditText etEventTitle = dialogView.findViewById(R.id.etEventTitle);
        EditText etEventDescription = dialogView.findViewById(R.id.etEventDescription);
        EditText etEventLocation = dialogView.findViewById(R.id.etEventLocation);
        EditText etEventFees = dialogView.findViewById(R.id.etEventFees);
        Button btnPickDate = dialogView.findViewById(R.id.btnPickDate);
        TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Initialize selected date
        final String[] selectedDate = {""}; // Store the selected date as a string

        if (event != null) {
            // Populate fields if editing an existing event
            etEventTitle.setText(event.getEvent_title());
            etEventDescription.setText(event.getEvent_description());
            etEventLocation.setText(event.getEvent_location());
            etEventFees.setText(event.getEvent_fees());
            selectedDate[0] = event.getEvent_date();
            tvSelectedDate.setText("Selected Date: " + selectedDate[0]);
        }

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // DatePickerDialog
        btnPickDate.setOnClickListener(v -> {
            // Get current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the selected date
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                        // Check if the selected date is in the past
                        if (selectedCalendar.before(calendar)) {
                            Toast.makeText(this, "Please select a date in the future", Toast.LENGTH_SHORT).show();
                        } else {
                            // Update the selected date
                            selectedDate[0] = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                            tvSelectedDate.setText("Selected Date: " + selectedDate[0]);
                        }
                    },
                    year, month, day
            );

            // Set the minimum date to today
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String title = etEventTitle.getText().toString().trim();
            String description = etEventDescription.getText().toString().trim();
            String location = etEventLocation.getText().toString().trim();
            String fees = etEventFees.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || location.isEmpty() || fees.isEmpty() || selectedDate[0].isEmpty()) {
                Toast.makeText(this, "Please fill all fields and select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (event == null) {
                // Add new event
                String eventId = eventsRef.push().getKey();
                EventModel2 newEvent = new EventModel2(eventId, title, description, location, fees, selectedDate[0]);
                eventsRef.child(eventId).setValue(newEvent)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show());
            } else {
                // Update existing event
                event.setEvent_title(title);
                event.setEvent_description(description);
                event.setEvent_location(location);
                event.setEvent_fees(fees);
                event.setEvent_date(selectedDate[0]);

                eventsRef.child(event.getEvent_ID()).setValue(event)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show());
            }
        });
    }
}