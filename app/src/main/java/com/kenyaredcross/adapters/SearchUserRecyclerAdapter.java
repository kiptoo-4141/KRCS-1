package com.kenyaredcross.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.activity.ChatActivity;
import com.kenyaredcross.domain_model.UserModel;

import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {

    private List<UserModel> userList;
    private final Context context;

    // Constructor with context passed from the Activity/Fragment
    public SearchUserRecyclerAdapter(Context context, List<UserModel> userList) {
        this.userList = userList;
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        holder.role.setText(user.getRole());

        // Ensure context and user object are not null before proceeding
        holder.itemView.setOnClickListener(view -> {
            if (context != null && user != null) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("recipientUsername", user.getUsername());  // Pass username
                context.startActivity(intent);
            } else {
                Log.e("SearchUserRecyclerAdapter", "Context or user is null. Cannot start ChatActivity.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // Method to update the data in the adapter
    public void updateData(List<UserModel> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }

    public static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView username, email, role;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            role = itemView.findViewById(R.id.role); // Added role TextView
        }
    }
}
