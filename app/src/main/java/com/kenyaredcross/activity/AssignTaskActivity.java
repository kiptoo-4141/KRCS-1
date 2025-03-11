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
import com.kenyaredcross.domain_model.Task;
import com.kenyaredcross.adapters.TaskAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignTaskActivity extends AppCompatActivity {

    private EditText taskDescription, startDate, endDate;
    private Spinner groupSpinner;
    private Button sendButton;
    private TextView groupMembersTextView;
    private RecyclerView assignedTasksRecyclerView;
    private List<String> groupNames;
    private List<String> groupIds;
    private List<String> groupMembers;
    private String selectedGroupId;
    private DatabaseReference databaseReference;
    private TaskAdapter taskAdapter;
    private List<Task> assignedTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);

        taskDescription = findViewById(R.id.taskDescription);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        groupSpinner = findViewById(R.id.groupSpinner);
        sendButton = findViewById(R.id.sendButton);
        groupMembersTextView = findViewById(R.id.groupMemberTextView);
        assignedTasksRecyclerView = findViewById(R.id.assignedTasksRecyclerView);

        groupNames = new ArrayList<>();
        groupIds = new ArrayList<>();
        groupMembers = new ArrayList<>();
        assignedTasks = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        setupDatePickers();
        loadGroups();
        loadAssignedTasks();

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGroupId = groupIds.get(position);
                loadGroupMembers(selectedGroupId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGroupId = null;
                groupMembersTextView.setText("");
            }
        });

        sendButton.setOnClickListener(v -> assignTask());

        assignedTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(assignedTasks);
        assignedTasksRecyclerView.setAdapter(taskAdapter);
    }

    private void setupDatePickers() {
        Calendar calendar = Calendar.getInstance();

        startDate.setOnClickListener(v -> showDatePickerDialog((datePicker, year, month, day) -> {
            startDate.setText(String.format("%d-%02d-%02d", year, month + 1, day));
        }, true)); // Restrict past dates

        endDate.setOnClickListener(v -> showDatePickerDialog((datePicker, year, month, day) -> {
            endDate.setText(String.format("%d-%02d-%02d", year, month + 1, day));
        }, false)); // No restriction
    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener, boolean restrictPastDates) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        if (restrictPastDates) {
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Restrict past dates
        }

        datePickerDialog.show();
    }

    private void loadGroups() {
        databaseReference.child("groups").addValueEventListener(new ValueEventListener() {
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

    private void loadGroupMembers(String groupId) {
        databaseReference.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMembers.clear();
                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    if (!memberSnapshot.getKey().equals("groupId") && !memberSnapshot.getKey().equals("groupName")) {
                        String username = memberSnapshot.child("username").getValue(String.class);
                        if (username != null) {
                            groupMembers.add(username);
                        }
                    }
                }
                groupMembersTextView.setText("Members: " + String.join(", ", groupMembers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignTaskActivity.this, "Failed to load group members: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAssignedTasks() {
        databaseReference.child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assignedTasks.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        assignedTasks.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AssignTaskActivity.this, "Failed to load tasks: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        if (!isEndDateAfterStartDate(start, end)) {
            Toast.makeText(this, "End date must be after start date.", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(taskDesc, start, end, selectedGroupId, "Pending");
        DatabaseReference tasksRef = databaseReference.child("tasks");
        String taskId = tasksRef.push().getKey();

        if (taskId != null) {
            tasksRef.child(taskId).setValue(task)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task assigned successfully.", Toast.LENGTH_SHORT).show();
                        taskDescription.setText("");
                        startDate.setText("");
                        endDate.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to assign task: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private boolean isEndDateAfterStartDate(String startDateStr, String endDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            return endDate != null && startDate != null && endDate.after(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}