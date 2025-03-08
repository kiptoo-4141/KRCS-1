package com.kenyaredcross.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.SupplyPaymentAdapter;

public class SupplyPaymentActivity extends AppCompatActivity {
    private RecyclerView supplyPaymentRecyclerView;
    private SupplyPaymentAdapter supplyPaymentAdapter;
    private String supplierEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_payment);

        // Get the logged-in supplier's email from the intent
        supplierEmail = getIntent().getStringExtra("supplierEmail");

        supplyPaymentRecyclerView = findViewById(R.id.SupplyPaymentView);
        supplyPaymentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        supplyPaymentAdapter = new SupplyPaymentAdapter(this, supplierEmail);
        supplyPaymentRecyclerView.setAdapter(supplyPaymentAdapter);
    }
}