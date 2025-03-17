package com.kenyaredcross.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.adapters.DonationAdapter;
import com.kenyaredcross.domain_model.Donation;

import java.util.ArrayList;
import java.util.List;

public class DonationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DonationAdapter adapter;
    private List<Donation> donationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donationList = new ArrayList<>();
        // Add your donations to the list
        donationList.add(new Donation("8d4c898a-2df6-4eaa-a294-fc22d3e89ba6", "56000", "2025-03-17 05:07:09", "kaje@gmail.com", "David Kaje", "Mobile Money", "0795310625", "pending"));

        adapter = new DonationAdapter(donationList, new DonationAdapter.OnItemClickListener() {
            @Override
            public void onApproveClick(int position) {
                Donation donation = donationList.get(position);
                donation.setStatus("approved");
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onRejectClick(int position) {
                Donation donation = donationList.get(position);
                donation.setStatus("rejected");
                adapter.notifyItemChanged(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_donations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_pending) {
            sortDonationsByStatus("pending");
            return true;
        } else if (id == R.id.action_sort_approved) {
            sortDonationsByStatus("approved");
            return true;
        } else if (id == R.id.action_sort_rejected) {
            sortDonationsByStatus("rejected");
            return true;
        } else if (id == R.id.action_search) {
            // Implement search functionality
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortDonationsByStatus(String status) {
        List<Donation> sortedList = new ArrayList<>();
        for (Donation donation : donationList) {
            if (donation.getStatus().equals(status)) {
                sortedList.add(donation);
            }
        }
        adapter = new DonationAdapter(sortedList, adapter.listener);
        recyclerView.setAdapter(adapter);
    }
}