package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kenyaredcross.R;

public class Supplier extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    FirebaseAuth auth;
    FirebaseUser user;

    CardView inventoryCard, requests, supplyPayment, reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reports = findViewById(R.id.SupplyReports);
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Supplier.this, SupplyReportsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the drawer layout and navigation view
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up card view click listeners
        supplyPayment = findViewById(R.id.SupplyPayment);
        supplyPayment.setOnClickListener(view -> {
            Intent intent = new Intent(Supplier.this, SupplyPaymentActivity.class);
            startActivity(intent);
        });

        requests = findViewById(R.id.FMRequestsCard);
        requests.setOnClickListener(view -> {
            Intent intent = new Intent(Supplier.this, SupplyRequestActivity.class);
            startActivity(intent);
        });

        inventoryCard = findViewById(R.id.supplyInventoryCrd);
        inventoryCard.setOnClickListener(view -> {
            Intent intent = new Intent(Supplier.this, SupplierInventoryActivity.class);
            startActivity(intent);
        });

        // Handle toolbar navigation
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            Intent intent = new Intent(Supplier.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            // Handle home menu item click if needed
        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(Supplier.this, ContactUsActivity.class);
            startActivity(intent);

            // Handle contact us menu item click if needed
        } else if (id == R.id.nav_log_out) {
            // Handle log out
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Supplier.this, Login.class));
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
