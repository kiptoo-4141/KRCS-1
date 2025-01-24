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
import com.kenyaredcross.adapters.CoursePaymentsAdapter;
import com.kenyaredcross.domain_model.CoursePayment;

import java.util.ArrayList;
import java.util.List;

public class CoursePaymentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CoursePaymentsAdapter adapter;
    private List<CoursePayment> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_payments);

        recyclerView = findViewById(R.id.recyclerViewCoursePayments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        paymentList = new ArrayList<>();
        adapter = new CoursePaymentsAdapter(paymentList);
        recyclerView.setAdapter(adapter);

        fetchPayments();
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
                            paymentList.add(payment);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }
}
