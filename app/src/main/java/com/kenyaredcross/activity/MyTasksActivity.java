package com.kenyaredcross.activity;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.VolunteerTasksAdapter;
import com.kenyaredcross.domain_model.VolunteerTasks;

import java.util.ArrayList;
import java.util.List;

public class MyTasksActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private VolunteerTasksAdapter taskAdapter;
    private List<VolunteerTasks> taskList;
    private DatabaseReference tasksRef, groupsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);

        // Initialize RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize task list and adapter
        taskList = new ArrayList<>();
        taskAdapter = new VolunteerTasksAdapter(taskList);
        tasksRecyclerView.setAdapter(taskAdapter);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");
        groupsRef = FirebaseDatabase.getInstance().getReference("groups");

        // Check if user is authenticated
        if (mAuth.getCurrentUser() == null) {
            Log.e("MyTasksActivity", "User is not authenticated");
            finish(); // Close the activity if the user is not logged in
            return;
        }

        // Load tasks for the authenticated user
        loadTasksForUser();
    }

    private void loadTasksForUser() {
        String userId = mAuth.getCurrentUser().getUid();
        Log.d("MyTasksActivity", "Loading tasks for user: " + userId);

        // Fetch groups the user is in
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("MyTasksActivity", "No groups found for the user");
                    return;
                }

                List<String> userGroupIds = new ArrayList<>();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    if (groupSnapshot.child(userId).exists()) {
                        userGroupIds.add(groupSnapshot.getKey());
                    }
                }
                Log.d("MyTasksActivity", "User Group IDs: " + userGroupIds);

                if (userGroupIds.isEmpty()) {
                    Log.d("MyTasksActivity", "User is not part of any groups");
                    return;
                }

                // Fetch tasks for the user's groups
                tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.d("MyTasksActivity", "No tasks found");
                            return;
                        }

                        taskList.clear();
                        for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                            VolunteerTasks task = taskSnapshot.getValue(VolunteerTasks.class);
                            if (task != null && userGroupIds.contains(task.getGroupId())) {
                                task.setTaskId(taskSnapshot.getKey()); // Set the task ID
                                taskList.add(task);
                            }
                        }
                        Log.d("MyTasksActivity", "Task List: " + taskList.toString());

                        if (taskList.isEmpty()) {
                            Log.d("MyTasksActivity", "No tasks found for the user's groups");
                        }

                        taskAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("MyTasksActivity", "Database Error: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MyTasksActivity", "Database Error: " + error.getMessage());
            }
        });
    }
}