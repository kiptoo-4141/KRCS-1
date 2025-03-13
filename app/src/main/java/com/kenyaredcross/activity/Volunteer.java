package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Volunteer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private CardView group, courses, tasks, myCourses, myCerts, attendanceCard;

    private static final int DOCUMENT_PICK_CODE = 1001;
    private List<Uri> documentUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteer);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");

        // Check if user profile exists
        checkUserProfile();

        // Set up window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        initializeUI();

        // Initialize drawer layout and navigation view
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeUI() {
        myCourses = findViewById(R.id.volunteermycourses);
        myCourses.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, MyCoursesActivity.class)));

        myCerts = findViewById(R.id.myCertificates);
        myCerts.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, CertActivity.class)));

        tasks = findViewById(R.id.myTasks);
        tasks.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, MyTasksActivity.class)));

        attendanceCard = findViewById(R.id.classAttendanceCard);
        attendanceCard.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, ClassAttendanceActivity.class)));

        group = findViewById(R.id.groupCard);
        group.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, MyGroupsActivity.class)));

        courses = findViewById(R.id.volunteercourses);
        courses.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, CoursesActivity.class)));
    }

    private void checkUserProfile() {
        String userId = auth.getCurrentUser().getUid();

        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // User profile exists, do nothing
            } else {
                // Show popup form for user details
                showUserProfilePopup();
            }
        });
    }

    private void showUserProfilePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Complete Your Profile");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_user_profile, null);
        builder.setView(dialogView);

        TextInputEditText fullName = dialogView.findViewById(R.id.fullName);
        TextInputEditText email = dialogView.findViewById(R.id.email);
        TextInputEditText location = dialogView.findViewById(R.id.location);
        TextInputEditText specialization = dialogView.findViewById(R.id.specialization);
        TextInputEditText phone = dialogView.findViewById(R.id.phone);
        Button btnUploadDocs = dialogView.findViewById(R.id.btnUploadDocs);
        RecyclerView rvDocuments = dialogView.findViewById(R.id.rvDocuments);

        // Auto-populate email
        email.setText(auth.getCurrentUser().getEmail());
        email.setEnabled(false);

        // Set up document upload
        btnUploadDocs.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, DOCUMENT_PICK_CODE);
        });

        // Set up RecyclerView for documents
        DocumentAdapter documentAdapter = new DocumentAdapter(documentUris);
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        rvDocuments.setAdapter(documentAdapter);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        dialog.setOnShowListener(d -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                String name = fullName.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userLocation = location.getText().toString().trim();
                String userSpecialization = specialization.getText().toString().trim();
                String userPhone = phone.getText().toString().trim();

                if (validateInputs(name, userLocation, userSpecialization, userPhone)) {
                    saveUserProfile(name, userEmail, userLocation, userSpecialization, userPhone);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private boolean validateInputs(String name, String location, String specialization, String phone) {
        if (name.isEmpty() || location.isEmpty() || specialization.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserProfile(String name, String email, String location, String specialization, String phone) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("email", email);
        userData.put("location", location);
        userData.put("specialization", specialization);
        userData.put("phone", phone);

        databaseReference.child(userId).setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Volunteer.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Volunteer.this, "Failed to save profile. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DOCUMENT_PICK_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        documentUris.add(data.getClipData().getItemAt(i).getUri());
                    }
                } else if (data.getData() != null) {
                    documentUris.add(data.getData());
                }
                // Update RecyclerView
                RecyclerView rvDocuments = findViewById(R.id.rvDocuments);
                if (rvDocuments != null) {
                    rvDocuments.getAdapter().notifyDataSetChanged();
                    rvDocuments.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            startActivity(new Intent(Volunteer.this, AboutUsActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(Volunteer.this, ContactUsActivity.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(Volunteer.this, HelpActivity.class));
        } else if (id == R.id.nav_log_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Volunteer.this, Login.class));
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