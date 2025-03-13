package com.kenyaredcross.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;

public class Trainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    CardView students, attendance, myclass, itemrqst, course;

    // Define userRole and userEmail
    private String userRole;
    private String userEmail;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the logged-in user's email
        userEmail = mAuth.getCurrentUser().getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch user role and details from the Users node
        fetchUserRoleAndDetails(userEmail);

        // Initialize views
        itemrqst = findViewById(R.id.requestItemCard);
        myclass = findViewById(R.id.classCard);
        course = findViewById(R.id.courseCard);
        attendance = findViewById(R.id.classAttendanceCard);

        // Set click listeners
        itemrqst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Trainer.this, RequestItemsActivity.class);
                startActivity(intent);
            }
        });

        myclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Trainer.this, MyClassesActivity.class);
                startActivity(intent);
            }
        });

        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Trainer.this, MyTeachingCoursesActivity.class);
                startActivity(intent);
            }
        });

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userRole != null && userEmail != null) {
                    Intent intent = new Intent(Trainer.this, ClassAttendanceActivity.class);
                    intent.putExtra("userRole", userRole); // Pass userRole
                    intent.putExtra("userEmail", userEmail); // Pass userEmail
                    startActivity(intent);
                } else {
                    Toast.makeText(Trainer.this, "User role or email not available!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize drawer layout and navigation view
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void fetchUserRoleAndDetails(String userEmail) {
        // Replace '.' with '_' in email to match Firebase key format
        String formattedEmail = userEmail.replace(".", "_");

        // Query the Users node
        mDatabase.child("Users").child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve user role and other details
                    userRole = dataSnapshot.child("role").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    Log.d("Trainer", "User Role: " + userRole);
                    Log.d("Trainer", "Username: " + username);
                    Log.d("Trainer", "Status: " + status);

                    // You can now use userRole and other details as needed
                } else {
                    Toast.makeText(Trainer.this, "User data not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Trainer.this, "Failed to fetch user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            Intent intent = new Intent(Trainer.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            // Handle home menu item click if needed
        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(Trainer.this, ContactUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(Trainer.this, HelpActivity.class));
        } else if (id == R.id.nav_log_out) {
            // Handle log out
            mAuth.signOut();
            startActivity(new Intent(Trainer.this, Login.class));
            finish(); // Optional: finish this activity
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