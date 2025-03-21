package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.MyClassesModel;

import java.util.ArrayList;
import java.util.List;

public class MyClassesActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private EditText searchEditText;
    private List<MyClassesModel> requestList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_classes);

        tableLayout = findViewById(R.id.tableLayout);
        searchEditText = findViewById(R.id.searchEditText);

        requestList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Enrollments");

        // Fetch data from Firebase
        fetchDataFromFirebase();

        // Search functionality
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRequests(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void fetchDataFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot courseSnapshot : userSnapshot.getChildren()) {
                        MyClassesModel request = courseSnapshot.getValue(MyClassesModel.class);
                        if (request != null) {
                            requestList.add(request);
                        }
                    }
                }
                updateTable(requestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyClassesActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTable(List<MyClassesModel> requests) {
        tableLayout.removeAllViews(); // Clear the table before adding new rows

        for (MyClassesModel request : requests) {
            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row_my_classes, null);

            TextView title = row.findViewById(R.id.tvTitle);
            TextView username = row.findViewById(R.id.tvUsername);
            TextView email = row.findViewById(R.id.tvEmail);
            TextView description = row.findViewById(R.id.tvDescription);
            TextView duration = row.findViewById(R.id.tvDuration);
            TextView status = row.findViewById(R.id.tvStatus);
            ImageView imageView = row.findViewById(R.id.ivCourseImage);
            Button btnApprove = row.findViewById(R.id.btnApprove);
            Button btnReject = row.findViewById(R.id.btnReject);

            title.setText(request.getTitle());
            username.setText("User: " + request.getUsername());
            email.setText("Email: " + request.getEmail());
            description.setText(request.getDescription());
            duration.setText("Duration: " + request.getDuration());
            status.setText("Status: " + request.getStatus());

            Glide.with(this).load(request.getImage()).into(imageView);

            btnApprove.setOnClickListener(v -> updateStatus(request, "passed"));
            btnReject.setOnClickListener(v -> updateStatus(request, "failed"));

            tableLayout.addView(row);
        }
    }

    private void updateStatus(MyClassesModel request, String newStatus) {
        DatabaseReference userEnrollmentsRef = FirebaseDatabase.getInstance()
                .getReference("Enrollments")
                .child(request.getEmail().replace(".", "_"));

        userEnrollmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    DatabaseReference courseStatusRef = courseSnapshot.getRef().child("status");
                    courseStatusRef.setValue(newStatus);
                }
                request.setStatus(newStatus);
                updateTable(requestList);
                Toast.makeText(MyClassesActivity.this, "Courses updated to " + newStatus, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyClassesActivity.this, "Failed to update courses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterRequests(String query) {
        List<MyClassesModel> filteredList = new ArrayList<>();
        for (MyClassesModel request : requestList) {
            if (request.getUsername().toLowerCase().contains(query.toLowerCase())) { // Fixed missing parenthesis
                filteredList.add(request);
            }
        }
        updateTable(filteredList);
    }
}