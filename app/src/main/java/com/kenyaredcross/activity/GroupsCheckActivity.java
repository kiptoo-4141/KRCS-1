package com.kenyaredcross.activity;

import android.os.Bundle;

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
import com.kenyaredcross.adapters.GroupsCheckAdapter;
import com.kenyaredcross.domain_model.GroupsCheckModel;

import java.util.ArrayList;
import java.util.List;

public class GroupsCheckActivity extends AppCompatActivity {

    private RecyclerView groupsView;
    private GroupsCheckAdapter adapter;
    private List<GroupsCheckModel> groupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_check);

        groupsView = findViewById(R.id.groupsView);
        groupsView.setLayoutManager(new LinearLayoutManager(this));

        groupList = new ArrayList<>();
        adapter = new GroupsCheckAdapter(groupList, this);
        groupsView.setAdapter(adapter);

        loadGroupsData();
    }

    private void loadGroupsData() {
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");
        groupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String groupId = groupSnapshot.getKey();
                    String groupName = groupSnapshot.child("groupName").getValue(String.class);

                    // Add group data to the list
                    GroupsCheckModel group = new GroupsCheckModel(groupId, "", "", groupName);
                    groupList.add(group);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
            }
        });
    }
}
