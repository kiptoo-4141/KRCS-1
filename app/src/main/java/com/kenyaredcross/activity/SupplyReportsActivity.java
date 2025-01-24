package com.kenyaredcross.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.SupplyReportsAdapter;
import com.kenyaredcross.domain_model.SupplyReportsModel;

import java.util.ArrayList;
import java.util.List;

public class SupplyReportsActivity extends AppCompatActivity {
    private RecyclerView supplyReportsView;
    private SupplyReportsAdapter supplyReportsAdapter;
    private List<SupplyReportsModel> supplyReportsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_reports);

        supplyReportsView = findViewById(R.id.SupplyReportsView);
        supplyReportsView.setLayoutManager(new LinearLayoutManager(this));
        supplyReportsList = new ArrayList<>();
        supplyReportsAdapter = new SupplyReportsAdapter(supplyReportsList);
        supplyReportsView.setAdapter(supplyReportsAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SupplyReports");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                supplyReportsList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SupplyReportsModel report = snapshot.getValue(SupplyReportsModel.class);
                    supplyReportsList.add(report);
                }
                supplyReportsAdapter.notifyDataSetChanged(); // Notify the adapter that data has changed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
