package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kenyaredcross.R;

public class FinanceManager extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CardView requests, reports, coursepayments, feed_backs,transaction, donations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finance_manager);

        // Set up window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize reports card and set onClick listener
//        reports = findViewById(R.id.FMreportsCard);
//        reports.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent reportsIntent = new Intent(FinanceManager.this, FMReportsActivity.class);
//                startActivity(reportsIntent);
//            }
//        });

        coursepayments = findViewById(R.id.FMCoursePaymentCard);
        coursepayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinanceManager.this, CoursePaymentsActivity.class);
                startActivity(intent);
            }
        });

        transaction = findViewById(R.id.FMtransactionCard);
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinanceManager.this,TransactionsActivity.class);
                startActivity(intent);
            }
        });

        donations = findViewById(R.id.FMdonationsCard);
        donations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinanceManager.this, DonationsActivity.class);
                startActivity(intent);
            }
        });

        feed_backs = findViewById(R.id.FMFeedbacks);
        feed_backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinanceManager.this, FeedbacksActivity.class);
                startActivity(intent);
            }
        });

        // Initialize requests card and set onClick listener
        requests = findViewById(R.id.FMRequestsCard);
        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinanceManager.this, FinanceRequestsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        // Handle navigation item selections
        if (id == R.id.nav_about_us2) {
            Intent intent = new Intent(FinanceManager.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            // Handle home menu item click if needed
        } else if (id ==R.id.nav_help) {
            startActivity(new Intent(FinanceManager.this, HelpActivity.class));
        }else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(FinanceManager.this, ContactUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            // Handle log out
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(FinanceManager.this, Login.class));
            finish(); // Optional: finish this activity to prevent returning to it
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
