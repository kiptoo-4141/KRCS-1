package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;

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
//import com.kenyaredcross.adapters.GroupAdapter;
import com.kenyaredcross.domain_model.Group;
import com.kenyaredcross.adapters.GroupAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyGroupsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        recyclerView = findViewById(R.id.recyclerViewMyGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList);
        recyclerView.setAdapter(groupAdapter);

        // Fetch logged-in user's email
        loggedInUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");

        fetchGroups();
    }

    private void fetchGroups() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : groupSnapshot.getChildren()) {
                        String emailKey = userSnapshot.getKey();
                        if (emailKey != null && emailKey.equals(loggedInUserEmail)) {
                            String groupId = groupSnapshot.getKey();
                            String groupName = groupSnapshot.child("groupName").getValue(String.class);
                            Group group = new Group(groupId, groupName);
                            groupList.add(group);
                            break;
                        }
                    }
                }
                groupAdapter.notifyDataSetChanged();
                if (groupList.isEmpty()) {
                    Toast.makeText(MyGroupsActivity.this, "No groups found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MyGroupsActivity.this, "Failed to fetch groups: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
