package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.FMReportsAdapter;
import com.kenyaredcross.domain_model.FMReportsModel;
import java.util.ArrayList;
import java.util.List;

public class FMReportsActivity extends AppCompatActivity {
    private RecyclerView reportsView;
    private FMReportsAdapter adapter;
    private List<FMReportsModel> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fmreports);

        reportsView = findViewById(R.id.reportsView);
        reportsView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        reports = new ArrayList<>();
        adapter = new FMReportsAdapter();
        reportsView.setAdapter(adapter);

        Spinner reportsType = findViewById(R.id.reportsType);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.report_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportsType.setAdapter(spinnerAdapter);

        reportsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);
                fetchReports(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void fetchReports(String reportType) {
        // Clear the existing reports
        reports.clear();

        // Fetch reports based on the selected type
        if (reportType.equals("FinanceReports")) {
            reports.add(new FMReportsModel("5000", "Finance", "2024-10-01", "Manager A", "Item A", "123", "Approved", "timestamp", null, null, null, null, null, 5));
        } else if (reportType.equals("DonationsReports")) {
            reports.add(new FMReportsModel(null, null, null, null, null, "donation123", null, null, "donationId123", "2024-10-01", "John Doe", "Mobile Money", "123456789", null));
        }
        // Additional report types can be added similarly

        // Notify the adapter of data changes
        adapter.notifyDataSetChanged();
    }
}
