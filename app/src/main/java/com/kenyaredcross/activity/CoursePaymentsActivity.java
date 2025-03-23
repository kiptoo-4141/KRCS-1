package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.CoursePayment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoursePaymentsActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private SearchView searchView;
    private List<CoursePayment> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_payments);

        tableLayout = findViewById(R.id.tableLayout);
        searchView = findViewById(R.id.searchView);
        paymentList = new ArrayList<>();

        fetchPayments();

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPayments(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPayments(newText);
                return false;
            }
        });
    }

    private void fetchPayments() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("CoursePayments");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paymentList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot paymentSnapshot : userSnapshot.getChildren()) {
                        CoursePayment payment = paymentSnapshot.getValue(CoursePayment.class);
                        if (payment != null) {
                            // Fetch username from Users node
                            fetchUsername(payment.getUserEmail(), payment);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void fetchUsername(String userEmail, CoursePayment payment) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userEmail.replace(".", "_"));

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    payment.setUsername(username != null ? username : "N/A");
                } else {
                    payment.setUsername("N/A");
                }
                paymentList.add(payment);
                populateTable(paymentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                payment.setUsername("N/A");
                paymentList.add(payment);
                populateTable(paymentList);
            }
        });
    }

    private void populateTable(List<CoursePayment> payments) {
        tableLayout.removeAllViews();

        // Add header row
        TableRow headerRow = new TableRow(this);
        addHeaderCell(headerRow, "Course ID");
        addHeaderCell(headerRow, "User Email");
        addHeaderCell(headerRow, "Username");
        addHeaderCell(headerRow, "Payment Method");
        addHeaderCell(headerRow, "Payment Details");
        addHeaderCell(headerRow, "Amount");
        addHeaderCell(headerRow, "Date");
        addHeaderCell(headerRow, "Time");
        addHeaderCell(headerRow, "Status");
        addHeaderCell(headerRow, "Transaction Code");
        addHeaderCell(headerRow, "Actions"); // New column for buttons
        tableLayout.addView(headerRow);

        // Add data rows
        for (CoursePayment payment : payments) {
            TableRow row = new TableRow(this);
            addCell(row, payment.getCourseId());
            addCell(row, payment.getUserEmail());
            addCell(row, payment.getUsername());
            addCell(row, payment.getPaymentMethod());
            addCell(row, payment.getPaymentDetails());
            addCell(row, "Ksh " + payment.getAmount());
            addCell(row, payment.getDate());
            addCell(row, payment.getTime());
            addCell(row, payment.getStatus());
            addCell(row, payment.getTransactionCode() != null ? payment.getTransactionCode() : "N/A");

            // Add Approve and Reject buttons
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 4, 4, 4);

            Button approveButton = new Button(this);
            approveButton.setText("Approve");
            approveButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            approveButton.setLayoutParams(params);
            approveButton.setOnClickListener(v -> {
                if (payment.getStatus().equalsIgnoreCase("Approved")) {
                    Toast.makeText(this, "Already approved", Toast.LENGTH_SHORT).show();
                    return;
                }
                updatePaymentStatus(payment, "Approved", row);
                addToEnrollments(payment);
            });

            Button rejectButton = new Button(this);
            rejectButton.setText("Reject");
            rejectButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            rejectButton.setLayoutParams(params);
            rejectButton.setOnClickListener(v -> {
                if (payment.getStatus().equalsIgnoreCase("Rejected")) {
                    Toast.makeText(this, "Already rejected", Toast.LENGTH_SHORT).show();
                    return;
                }
                updatePaymentStatus(payment, "Rejected", row);
            });

            // Add buttons to a horizontal LinearLayout
            LinearLayout buttonLayout = new LinearLayout(this);
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.addView(approveButton);
            buttonLayout.addView(rejectButton);

            // Add the button layout to the table row
            row.addView(buttonLayout);
            tableLayout.addView(row);
        }
    }

    private void updatePaymentStatus(CoursePayment payment, String newStatus, TableRow row) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("CoursePayments")
                .child(payment.getUserEmail().replace(".", "_"))
                .child(payment.getCourseId());

        dbRef.child("status").setValue(newStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update the status in the table row
                TextView statusCell = (TextView) row.getChildAt(8); // Status is the 9th column (index 8)
                statusCell.setText(newStatus);
                Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void addToEnrollments(CoursePayment payment) {
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

                    if (courseId != null && courseId.equals(payment.getCourseId())) {
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
                                Toast.makeText(CoursePaymentsActivity.this, "Added to Enrollments", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CoursePaymentsActivity.this, "Failed to add to Enrollments", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(CoursePaymentsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }
                Toast.makeText(CoursePaymentsActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CoursePaymentsActivity.this, "Error retrieving course data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setBackgroundResource(R.drawable.table_header_border);
        textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        row.addView(textView);
    }

    private void addCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setBackgroundResource(R.drawable.table_cell_border);
        textView.setTextAppearance(this, android.R.style.TextAppearance_Small);
        row.addView(textView);
    }

    private void filterPayments(String query) {
        List<CoursePayment> filteredList = new ArrayList<>();
        for (CoursePayment payment : paymentList) {
            if (payment.getStatus().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(payment);
            }
        }
        populateTable(filteredList);
    }
}