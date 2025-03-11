package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
    private FirebaseAuth auth;
    private FirebaseUser user;
    private CardView inventoryCard, requests, supplyPayment, reports, feeds;

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

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        inventoryCard = findViewById(R.id.supplyInventoryCrd);
        requests = findViewById(R.id.FMRequestsCard);
        supplyPayment = findViewById(R.id.SupplyPayment);
        reports = findViewById(R.id.SupplyReports);
        feeds = findViewById(R.id.feedss);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up navigation drawer
        navigationView.setNavigationItemSelectedListener(this);

        // Set click listeners for card views
        inventoryCard.setOnClickListener(v -> startActivity(new Intent(Supplier.this, SupplierInventoryActivity.class)));
        // Inside the Supplier activity
        requests.setOnClickListener(v -> {
            if (user != null) {
                String userEmail = user.getEmail(); // Get the logged-in user's email
                Intent intent = new Intent(Supplier.this, SupplyRequestActivity.class);
                intent.putExtra("supplierEmail", userEmail); // Pass the email to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
        // Inside the Supplier activity
        supplyPayment.setOnClickListener(v -> {
            if (user != null) {
                String userEmail = user.getEmail(); // Get the logged-in user's email
                Intent intent = new Intent(Supplier.this, SupplyPaymentActivity.class);
                intent.putExtra("supplierEmail", userEmail); // Pass the email to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
        reports.setOnClickListener(v -> startActivity(new Intent(Supplier.this, SupplyReportsActivity.class)));
        feeds.setOnClickListener(v -> startActivity(new Intent(Supplier.this, FeedbacksActivity.class)));

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
            startActivity(new Intent(this, AboutUsActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(this, ContactUsActivity.class));
        } else if (id ==R.id.nav_help) {
            startActivity(new Intent(Supplier.this, HelpActivity.class));
        }else if (id == R.id.nav_log_out) {
            auth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        }

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