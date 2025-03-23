package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.RequestModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestsActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private List<RequestModel> requestList;
    private DatabaseReference databaseReference;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        tableLayout = findViewById(R.id.tableLayout);
        searchView = findViewById(R.id.searchView);

        requestList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Enrollments");

        loadPendingRequests();

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRequests(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRequests(newText);
                return false;
            }
        });
    }

    private void loadPendingRequests() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot enrollmentSnapshot : userSnapshot.getChildren()) {
                        RequestModel request = enrollmentSnapshot.getValue(RequestModel.class);
                        if (request != null && "pending".equals(request.getStatus())) {
                            request.setUserId(userSnapshot.getKey()); // Store the user ID (email with underscores)
                            request.setCourseId(enrollmentSnapshot.getKey()); // Store the course ID
                            requestList.add(request);
                        }
                    }
                }
                populateTable(requestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateTable(List<RequestModel> requests) {
        tableLayout.removeAllViews();

        // Add column titles
        TableRow titleRow = new TableRow(this);
        titleRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        addTitleToRow(titleRow, "Title");
        addTitleToRow(titleRow, "Username");
        addTitleToRow(titleRow, "Email");
        addTitleToRow(titleRow, "Description");
        addTitleToRow(titleRow, "Duration");
        addTitleToRow(titleRow, "Status");
        addTitleToRow(titleRow, "Actions");

        tableLayout.addView(titleRow);

        // Add data rows
        for (RequestModel request : requests) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            addCellToRow(row, request.getTitle());
            addCellToRow(row, request.getUsername());
            addCellToRow(row, request.getEmail());
            addCellToRow(row, request.getDescription());
            addCellToRow(row, request.getDuration());
            addCellToRow(row, request.getStatus());

            // Add Approve and Reject buttons
            TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            buttonParams.setMargins(4, 4, 4, 4);

            Button btnApprove = new Button(this);
            btnApprove.setText("Approve");
            btnApprove.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            btnApprove.setTextColor(getResources().getColor(android.R.color.white));
            btnApprove.setLayoutParams(buttonParams);
            btnApprove.setOnClickListener(v -> updateRequestStatus(request, "approved"));

            Button btnReject = new Button(this);
            btnReject.setText("Reject");
            btnReject.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            btnReject.setTextColor(getResources().getColor(android.R.color.white));
            btnReject.setLayoutParams(buttonParams);
            btnReject.setOnClickListener(v -> updateRequestStatus(request, "rejected"));

            TableRow buttonRow = new TableRow(this);
            buttonRow.addView(btnApprove);
            buttonRow.addView(btnReject);

            row.addView(buttonRow);
            tableLayout.addView(row);
        }
    }

    private void addTitleToRow(TableRow row, String title) {
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.table_border); // Add border to the cell
        textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        row.addView(textView);
    }

    private void addCellToRow(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.table_border); // Add border to the cell
        row.addView(textView);
    }

    private void filterRequests(String query) {
        List<RequestModel> filteredList = new ArrayList<>();
        for (RequestModel request : requestList) {
            if (request.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    request.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    request.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(request);
            }
        }
        populateTable(filteredList);
    }

    private void updateRequestStatus(RequestModel request, String newStatus) {
        // Construct the path to the specific enrollment request
        DatabaseReference requestRef = databaseReference
                .child(request.getUserId()) // User node (email with underscores)
                .child(request.getCourseId()); // Course ID node

        // Update the status field
        requestRef.child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    // If the status is "approved", add the current date as the startDate
                    if ("approved".equals(newStatus)) {
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        requestRef.child("startDate").setValue(currentDate)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(RequestsActivity.this, "Request approved and start date set to " + currentDate, Toast.LENGTH_SHORT).show();
                                    loadPendingRequests(); // Refresh the list
                                })
                                .addOnFailureListener(e -> Toast.makeText(RequestsActivity.this, "Failed to set start date", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(RequestsActivity.this, "Request " + newStatus, Toast.LENGTH_SHORT).show();
                        loadPendingRequests(); // Refresh the list
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(RequestsActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }
}