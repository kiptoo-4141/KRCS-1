package com.kenyaredcross.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kenyaredcross.R;

public class Youth extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    CardView courses, youthDonationCard, myCourses, volunteeringCard,events, messaging, receipt, cert;

    Toolbar toolbar;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Set up window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        cert = findViewById(R.id.youthCertificationCard);
        cert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Youth.this, MyCompletedCoursesActivity.class);
                startActivity(intent);
            }
        });

        messaging = findViewById(R.id.youthMessagingCard);
        messaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Youth.this,FeedbacksActivity.class);
                startActivity(intent);
            }
        });

        receipt = findViewById(R.id.youthReceiptCards);
        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Youth.this, YTReceiptsActivity.class);
                startActivity(intent);
            }
        });

        events = findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Youth.this, EventsActivity.class);
                startActivity(intent);
            }
        });

        // Initialize CardViews
//        volunteeringCard = findViewById(R.id.youthVolunteeringCard);
//        volunteeringCard.setOnClickListener(v -> {
//            Intent intent = new Intent(Youth.this, VolunteeringActivity.class);
//            startActivity(intent);
//        });

        myCourses = findViewById(R.id.mycourses);
        myCourses.setOnClickListener(v -> {
            Intent intent = new Intent(Youth.this, MyCoursesActivity.class);
            startActivity(intent);
        });

        courses = findViewById(R.id.courses);
        courses.setOnClickListener(v -> {
            Intent intent = new Intent(Youth.this, CoursesActivity.class);
            startActivity(intent);
        });

        youthDonationCard = findViewById(R.id.youthDonationCard);
        youthDonationCard.setOnClickListener(v -> {
            Intent intent = new Intent(Youth.this, DonationActivity.class);
            startActivity(intent);
        });

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            Intent intent = new Intent(Youth.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            // Handle home menu item click if needed
        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(Youth.this, ContactUsActivity.class);
            startActivity(intent);

            // Handle contact us menu item click if needed
        } else if (id == R.id.nav_log_out) {
            // Handle log out
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Youth.this, Login.class));
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
