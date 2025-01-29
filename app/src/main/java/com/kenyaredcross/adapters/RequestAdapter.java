package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.RequestModel;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private Context context;
    private List<RequestModel> requestList;

    public RequestAdapter(Context context, List<RequestModel> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModel request = requestList.get(position);
        holder.title.setText(request.getTitle());
        holder.username.setText("User: " + request.getUsername());
        holder.email.setText("Email: " + request.getEmail());
        holder.description.setText(request.getDescription());
        holder.duration.setText("Duration: " + request.getDuration());
        holder.status.setText("Status: " + request.getStatus());

        Glide.with(context).load(request.getImage()).into(holder.imageView);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Enrollments")
                .child(request.getEmail().replace(".", "_"))
                .child("101");

        holder.btnApprove.setOnClickListener(v -> updateStatus(databaseReference, request, "approved"));
        holder.btnReject.setOnClickListener(v -> updateStatus(databaseReference, request, "rejected"));
    }

    private void updateStatus(DatabaseReference databaseReference, RequestModel request, String newStatus) {
        databaseReference.child("status").setValue(newStatus);
        request.setStatus(newStatus);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, username, email, description, duration, status;
        ImageView imageView;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            username = itemView.findViewById(R.id.tvUsername);
            email = itemView.findViewById(R.id.tvEmail);
            description = itemView.findViewById(R.id.tvDescription);
            duration = itemView.findViewById(R.id.tvDuration);
            status = itemView.findViewById(R.id.tvStatus);
            imageView = itemView.findViewById(R.id.ivCourseImage);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}