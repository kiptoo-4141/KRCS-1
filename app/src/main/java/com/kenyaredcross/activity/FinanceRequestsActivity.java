package com.kenyaredcross.activity;

import android.os.Bundle;
import android.widget.Toast;
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
import com.kenyaredcross.adapters.FinanceRequestAdapter;
import com.kenyaredcross.domain_model.FinanceRequestModel;
import java.util.ArrayList;
import java.util.List;

public class FinanceRequestsActivity extends AppCompatActivity {
    private RecyclerView financeRequestView;
    private FinanceRequestAdapter adapter;
    private List<FinanceRequestModel> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_requests);

        financeRequestView = findViewById(R.id.FinanceRequestView);
        financeRequestView.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();
        adapter = new FinanceRequestAdapter(this, requestList);
        financeRequestView.setAdapter(adapter);

        fetchPendingRequests();
    }

    private void fetchPendingRequests() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FinanceRequests");

        databaseReference.orderByChild("status").equalTo("pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FinanceRequestModel request = snapshot.getValue(FinanceRequestModel.class);
                            requestList.add(request);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(FinanceRequestsActivity.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
