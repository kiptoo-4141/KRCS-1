package com.kenyaredcross.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.kenyaredcross.adapters.PendingTaskAdapter;
import com.kenyaredcross.domain_model.PendingTask;

import java.util.ArrayList;
import java.util.List;

public class PendingTaskActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PendingTaskAdapter pendingTaskAdapter;
    private List<PendingTask> pendingTaskList;
    private DatabaseReference tasksRef;
    private final String groupId = "3JSAP5W"; // Change dynamically based on logged-in coordinator

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_task);

        recyclerView = findViewById(R.id.pendingrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingTaskList = new ArrayList<>();
        pendingTaskAdapter = new PendingTaskAdapter(this, pendingTaskList, groupId);
        recyclerView.setAdapter(pendingTaskAdapter);

        tasksRef = FirebaseDatabase.getInstance().getReference("pendingtasks").child(groupId);
        loadPendingTasks();
    }

    private void loadPendingTasks() {
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingTaskList.clear();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    PendingTask task = taskSnapshot.getValue(PendingTask.class);
                    pendingTaskList.add(task);
                }
                pendingTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingTaskActivity.this, "Failed to load pending tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
