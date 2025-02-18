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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.MyClassesModel;

import java.util.List;

public class MyClassesAdapter extends RecyclerView.Adapter<MyClassesAdapter.ViewHolder> {
    private final Context context;
    private final List<MyClassesModel> requestList;

    public MyClassesAdapter(Context context, List<MyClassesModel> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyClassesModel request = requestList.get(position);
        holder.title.setText(request.getTitle());
        holder.username.setText("User: " + request.getUsername());
        holder.email.setText("Email: " + request.getEmail());
        holder.description.setText(request.getDescription());
        holder.duration.setText("Duration: " + request.getDuration());
        holder.status.setText("Status: " + request.getStatus());

        Glide.with(context).load(request.getImage()).into(holder.imageView);

        DatabaseReference userEnrollmentsRef = FirebaseDatabase.getInstance()
                .getReference("Enrollments")
                .child(request.getEmail().replace(".", "_"));

        holder.btnApprove.setOnClickListener(v -> updateAllCoursesStatus(userEnrollmentsRef, request, "passed"));
        holder.btnReject.setOnClickListener(v -> updateAllCoursesStatus(userEnrollmentsRef, request, "failed"));
    }

    private void updateAllCoursesStatus(DatabaseReference userEnrollmentsRef, MyClassesModel request, String newStatus) {
        userEnrollmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    DatabaseReference courseStatusRef = courseSnapshot.getRef().child("status");
                    courseStatusRef.setValue(newStatus);
                }
                request.setStatus(newStatus);
                notifyDataSetChanged();
                Toast.makeText(context, "courses updated to " + newStatus, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to update courses", Toast.LENGTH_SHORT).show();
            }
        });
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
