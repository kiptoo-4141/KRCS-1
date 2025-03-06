package com.kenyaredcross.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.CoursesModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CoursesAdapter extends FirebaseRecyclerAdapter<CoursesModel, CoursesAdapter.myViewHolder> {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference;

    public CoursesAdapter(@NonNull FirebaseRecyclerOptions<CoursesModel> options) {
        super(options);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull CoursesModel coursesModel) {

        holder.courseId.setText(coursesModel.getId());
        holder.courseTitle.setText(coursesModel.getTitle());
        holder.courseDescription.setText(coursesModel.getDescription());
        holder.courseDuration.setText(coursesModel.getDuration());
//        holder.certificationStatus.setText(coursesModel.getCertification_status());
        holder.fees.setText("Kshs " + coursesModel.getFees().toString());

        Glide.with(holder.img.getContext())
                .load(coursesModel.getImage_link())
                .placeholder(R.drawable.baseline_question_mark_24)
                .circleCrop()
                .error(R.drawable.baseline_question_mark_24)
                .into(holder.img);

        // Handle Enroll Button Click
        holder.enrollButton.setOnClickListener(v -> {
            if (user != null) {
                String userEmailKey = user.getEmail().replace(".", "_"); // Use email as key (replacing dots)

                // Check if the user is already enrolled in the course
                databaseReference.child("Enrollments").child(userEmailKey).child(coursesModel.getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(holder.enrollButton.getContext(), "Already enrolled in this course", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Show payment dialog
                                    showPaymentDialog(holder.enrollButton.getContext(), holder, coursesModel, userEmailKey);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(holder.enrollButton.getContext(), "Failed to check enrollment", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(holder.enrollButton.getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPaymentDialog(Context context, myViewHolder holder, CoursesModel coursesModel, String userEmailKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Payment Confirmation")
                .setMessage("Do you want to pay the course fees?")
                .setPositiveButton("Yes", (dialog, which) -> showPaymentMethodDialog(context, holder, coursesModel, userEmailKey))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showPaymentMethodDialog(Context context, myViewHolder holder, CoursesModel coursesModel, String userEmailKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Payment Method")
                .setItems(new String[]{"Bank Transfer", "M-Pesa"}, (dialog, which) -> {
                    if (which == 0) {
                        requestPaymentDetails(context, "Bank Account Number", 16, holder, coursesModel, userEmailKey, "Bank Transfer");
                    } else {
                        requestPaymentDetails(context, "M-Pesa Number", 10, holder, coursesModel, userEmailKey, "M-Pesa");
                    }
                })
                .show();
    }

    private void requestPaymentDetails(Context context, String detailType, int maxLength, myViewHolder holder, CoursesModel coursesModel, String userEmailKey, String paymentMethod) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_payment_details, null);
        EditText inputField = view.findViewById(R.id.paymentDetailInput);
        inputField.setHint(detailType);

        builder.setView(view)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String paymentDetail = inputField.getText().toString().trim();
                    if (paymentDetail.length() == maxLength) {
                        recordPayment(holder, coursesModel, userEmailKey, paymentMethod, paymentDetail);
                    } else {
                        Toast.makeText(context, detailType + " must be exactly " + maxLength + " digits", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void updateOptions(FirebaseRecyclerOptions<CoursesModel> options) {
        updateOptions(options);
    }


    private void recordPayment(myViewHolder holder, CoursesModel coursesModel, String userEmailKey, String paymentMethod, String paymentDetail) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        HashMap<String, Object> paymentData = new HashMap<>();
        paymentData.put("userEmail", user.getEmail());
        paymentData.put("courseId", coursesModel.getId());
        paymentData.put("amount", coursesModel.getFees());
        paymentData.put("paymentMethod", paymentMethod);
        paymentData.put("paymentDetails", paymentDetail);
        paymentData.put("status", "Pending");
        paymentData.put("date", currentDate);
        paymentData.put("time", currentTime);

        databaseReference.child("CoursePayments").child(userEmailKey).child(coursesModel.getId()).setValue(paymentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(holder.enrollButton.getContext(), "Payment recorded. Await confirmation.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.enrollButton.getContext(), "Payment recording failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courses_item, parent, false);
        return new myViewHolder(view);
    }

    static class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView courseId, courseTitle, courseDescription, courseDuration, certificationStatus, fees;
        Button enrollButton;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.courseimg);
            courseId = itemView.findViewById(R.id.courseId);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            courseDuration = itemView.findViewById(R.id.courseDuration);
//            certificationStatus = itemView.findViewById(R.id.certificationStatus);
            enrollButton = itemView.findViewById(R.id.enrollButton);
            fees = itemView.findViewById(R.id.feesAmount);
        }
    }
}
