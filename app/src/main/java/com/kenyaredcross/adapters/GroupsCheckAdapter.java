package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.GroupsCheckModel;

import java.util.List;

public class GroupsCheckAdapter extends RecyclerView.Adapter<GroupsCheckAdapter.GroupViewHolder> {

    private final List<GroupsCheckModel> groupList;
    private final Context context;

    public GroupsCheckAdapter(List<GroupsCheckModel> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_check_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupsCheckModel group = groupList.get(position);

        holder.groupName.setText(group.getGroupName());
        holder.groupId.setText(group.getGroupId());

        // Retrieve the number of members in the group
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups")
                .child(group.getGroupId()); // Use groupId to locate the group

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long membersCount = snapshot.getChildrenCount() - 1; // Excluding the groupName field
                holder.membersCount.setText(String.valueOf(membersCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load group data", Toast.LENGTH_SHORT).show();
            }
        });

        holder.viewButton.setOnClickListener(v -> {
            // Display all members in the group when the "View" button is clicked
            displayMembers(group.getGroupId());
        });
    }

    private void displayMembers(String groupId) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups")
                .child(groupId);

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder memberInfo = new StringBuilder();

                // Iterate through all members in the group
                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    if (!memberSnapshot.getKey().equals("groupName")) { // Exclude group name
                        String email = memberSnapshot.child("email").getValue(String.class);
                        String username = memberSnapshot.child("username").getValue(String.class);

                        // Append member information
                        memberInfo.append("Email: ").append(email).append("\n")
                                .append("Username: ").append(username).append("\n\n");
                    }
                }

                if (memberInfo.length() > 0) {
                    // Display members' information in a Toast (you can customize this to show in a dialog or new activity)
                    Toast.makeText(context, memberInfo.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "No members found in this group.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load members", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName, groupId, membersCount;
        View viewButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupname);
            groupId = itemView.findViewById(R.id.groupid);
            membersCount = itemView.findViewById(R.id.membersCount);
            viewButton = itemView.findViewById(R.id.viewbtn);
        }
    }
}
