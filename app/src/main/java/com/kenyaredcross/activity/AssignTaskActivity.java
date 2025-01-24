package com.kenyaredcross.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AssignTaskActivity extends AppCompatActivity {

    private EditText taskDescription, startDate, endDate;
    private Spinner groupSpinner;
    private Button sendButton;
    private List<String> groupNames;
    private List<String> groupIds;
    private String selectedGroupId;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);

        taskDescription = findViewById(R.id.taskDescription);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        groupSpinner = findViewById(R.id.groupSpinner);
        sendButton = findViewById(R.id.sendButton);

        groupNames = new ArrayList<>();
        groupIds = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("groups");

        setupDatePickers();
        loadGroups();

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroupId = groupIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroupId = null;
            }
        });

        sendButton.setOnClickListener(v -> assignTask());
    }

    private void setupDatePickers() {
        startDate.setOnClickListener(v -> showDatePickerDialog((datePicker, year, month, day) -> {
            startDate.setText(String.format("%d-%02d-%02d", year, month + 1, day));
        }));

        endDate.setOnClickListener(v -> showDatePickerDialog((datePicker, year, month, day) -> {
            endDate.setText(String.format("%d-%02d-%02d", year, month + 1, day));
        }));
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadGroups() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupNames.clear();
                groupIds.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String groupName = groupSnapshot.child("groupName").getValue(String.class);
                    if (groupName != null) {
                        groupNames.add(groupName);
                        groupIds.add(groupSnapshot.getKey());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AssignTaskActivity.this, android.R.layout.simple_spinner_item, groupNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                groupSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignTaskActivity.this, "Failed to load groups: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignTask() {
        String taskDesc = taskDescription.getText().toString().trim();
        String start = startDate.getText().toString().trim();
        String end = endDate.getText().toString().trim();

        if (taskDesc.isEmpty() || start.isEmpty() || end.isEmpty() || selectedGroupId == null) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(taskDesc, start, end, selectedGroupId);
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("tasks");
        String taskId = tasksRef.push().getKey();

        if (taskId != null) {
            tasksRef.child(taskId).setValue(task)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Task assigned successfully.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to assign task: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
