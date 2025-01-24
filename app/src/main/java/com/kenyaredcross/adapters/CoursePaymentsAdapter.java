package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.kenyaredcross.domain_model.CoursePayment;

import java.util.HashMap;
import java.util.List;

public class CoursePaymentsAdapter extends RecyclerView.Adapter<CoursePaymentsAdapter.ViewHolder> {

    private final List<CoursePayment> paymentList;

    public CoursePaymentsAdapter(List<CoursePayment> paymentList) {
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoursePayment payment = paymentList.get(position);

        // Bind data to UI elements
        holder.courseId.setText(payment.getCourseId());
        holder.userEmail.setText(payment.getUserEmail());
        holder.paymentMethod.setText(payment.getPaymentMethod());
        holder.paymentDetails.setText(payment.getPaymentDetails());
        holder.amount.setText("Ksh " + payment.getAmount());
        holder.date.setText(payment.getDate());
        holder.time.setText(payment.getTime());
        holder.status.setText(payment.getStatus());

        // Approve button logic
        holder.approveButton.setOnClickListener(v -> {
            if (payment.getStatus().equalsIgnoreCase("Approved")) {
                Toast.makeText(holder.itemView.getContext(), "Already approved", Toast.LENGTH_SHORT).show();
                return;
            }
            updatePaymentStatus(payment, "Approved", holder);
            addToEnrollments(payment, holder.itemView.getContext());
        });

        // Reject button logic
        holder.rejectButton.setOnClickListener(v -> {
            if (payment.getStatus().equalsIgnoreCase("Rejected")) {
                Toast.makeText(holder.itemView.getContext(), "Already rejected", Toast.LENGTH_SHORT).show();
                return;
            }
            updatePaymentStatus(payment, "Rejected", holder);
        });
    }

    private void updatePaymentStatus(CoursePayment payment, String newStatus, ViewHolder holder) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("CoursePayments").child(payment.getUserEmail().replace(".", "_"))
                .child(payment.getCourseId());

        dbRef.child("status").setValue(newStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                holder.status.setText(newStatus);
                Toast.makeText(holder.itemView.getContext(), "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(holder.itemView.getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(holder.itemView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void addToEnrollments(CoursePayment payment, Context context) {
        DatabaseReference coursesRef = FirebaseDatabase.getInstance().getReference().child("Courses");
        DatabaseReference enrollmentsRef = FirebaseDatabase.getInstance().getReference()
                .child("Enrollments")
                .child(payment.getUserEmail().replace(".", "_"))
                .child(payment.getCourseId());

        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String courseId = courseSnapshot.child("id").getValue(String.class);
                    String title = courseSnapshot.child("title").getValue(String.class);

                    if (courseId != null && (courseId.equals(payment.getCourseId()) || title.equals(payment.getCourseId()))) {
                        // Retrieve course data
                        String certificationStatus = courseSnapshot.child("certification_status").getValue(String.class);
                        String description = courseSnapshot.child("description").getValue(String.class);
                        String duration = courseSnapshot.child("duration").getValue(String.class);
                        String image = courseSnapshot.child("image_link").getValue(String.class);

                        // Prepare enrollment data
                        HashMap<String, Object> enrollmentData = new HashMap<>();
                        enrollmentData.put("certificationStatus", certificationStatus != null ? certificationStatus : "N/A");
                        enrollmentData.put("description", description != null ? description : "No description available");
                        enrollmentData.put("duration", duration != null ? duration : "N/A");
                        enrollmentData.put("image", image != null ? image : "N/A");
                        enrollmentData.put("status", "pending");
                        enrollmentData.put("title", title != null ? title : "N/A");
                        enrollmentData.put("username", payment.getUserEmail().split("@")[0]);
                        enrollmentData.put("email", payment.getUserEmail());

                        // Add to enrollments
                        enrollmentsRef.setValue(enrollmentData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Added to Enrollments", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to add to Enrollments", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }
                Toast.makeText(context, "Course not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error retrieving course data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseId, userEmail, paymentMethod, paymentDetails, amount, date, time, status;
        Button approveButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseId = itemView.findViewById(R.id.textCourseId);
            userEmail = itemView.findViewById(R.id.textUserEmail);
            paymentMethod = itemView.findViewById(R.id.textPaymentMethod);
            paymentDetails = itemView.findViewById(R.id.textPaymentDetails);
            amount = itemView.findViewById(R.id.textAmount);
            date = itemView.findViewById(R.id.textDate);
            time = itemView.findViewById(R.id.textTime);
            status = itemView.findViewById(R.id.textStatus);
            approveButton = itemView.findViewById(R.id.buttonApprove);
            rejectButton = itemView.findViewById(R.id.buttonReject);
        }
    }
}
