package com.kenyaredcross.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kenyaredcross.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Volunteer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private CardView group, courses, tasks, myCourses, myCerts, attendanceCard, messages;

    private static final int DOCUMENT_PICK_CODE = 1001;
    private List<Uri> documentUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteer);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

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

        messages = findViewById(R.id.volunteerMessages);
            messages.setOnClickListener(v -> startActivity(new Intent(Volunteer.this, FeedbacksActivity.class)));

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
        TextInputEditText idNumber = dialogView.findViewById(R.id.idNumber);
        TextInputEditText dob = dialogView.findViewById(R.id.dob);
        Button btnUploadDocs = dialogView.findViewById(R.id.btnUploadDocs);
        RecyclerView rvDocuments = dialogView.findViewById(R.id.rvDocuments);

        // Auto-populate email
        email.setText(auth.getCurrentUser().getEmail());
        email.setEnabled(false);

        // Retrieve full name from Users node
        final String[] userEmail = {auth.getCurrentUser().getEmail().replace(".", "_")};
        databaseReference.getParent().child("Users").child(userEmail[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    fullName.setText(username);
                    fullName.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Volunteer.this, "Error retrieving username: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up date picker for DOB
        dob.setOnClickListener(v -> showDatePicker(dob));

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
            Button saveButton = dialog.findViewById(R.id.btnSave);
            saveButton.setOnClickListener(v -> {
                String name = fullName.getText().toString().trim();
                userEmail[0] = email.getText().toString().trim();
                String userLocation = location.getText().toString().trim();
                String userSpecialization = specialization.getText().toString().trim();
                String userPhone = phone.getText().toString().trim();
                String userIdNumber = idNumber.getText().toString().trim();
                String userDob = dob.getText().toString().trim();

                if (validateInputs(name, userLocation, userSpecialization, userPhone, userIdNumber, userDob)) {
                    checkUniquenessAndSave(name, userEmail[0], userLocation, userSpecialization, userPhone, userIdNumber, userDob, dialog);
                }
            });
        });

        dialog.show();
    }

    private void showDatePicker(TextInputEditText dob) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            if (selectedDate.after(Calendar.getInstance())) {
                Toast.makeText(Volunteer.this, "Date of Birth cannot be in the future", Toast.LENGTH_SHORT).show();
            } else {
                dob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private boolean validateInputs(String name, String location, String specialization, String phone, String idNumber, String dob) {
        if (name.isEmpty() || location.isEmpty() || specialization.isEmpty() || phone.isEmpty() || idNumber.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkUniquenessAndSave(String name, String email, String location, String specialization, String phone, String idNumber, String dob, AlertDialog dialog) {
        databaseReference.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Volunteer.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.orderByChild("idNumber").equalTo(idNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(Volunteer.this, "ID number already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                saveUserProfile(name, email, location, specialization, phone, idNumber, dob, dialog);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Volunteer.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Volunteer.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile(String name, String email, String location, String specialization, String phone, String idNumber, String dob, AlertDialog dialog) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("email", email);
        userData.put("location", location);
        userData.put("specialization", specialization);
        userData.put("phone", phone);
        userData.put("idNumber", idNumber);
        userData.put("dob", dob);

        // Upload documents to Firestore and store links
        if (!documentUris.isEmpty()) {
            List<String> documentUrls = new ArrayList<>();
            for (Uri uri : documentUris) {
                StorageReference fileRef = storageReference.child("documents/" + System.currentTimeMillis() + "_" + uri.getLastPathSegment());
                UploadTask uploadTask = fileRef.putFile(uri);
                uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    documentUrls.add(uri1.toString());
                    if (documentUrls.size() == documentUris.size()) {
                        userData.put("documents", documentUrls);
                        saveProfileToDatabase(userId, userData, dialog);
                    }
                })).addOnFailureListener(e -> Toast.makeText(Volunteer.this, "Failed to upload document: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } else {
            saveProfileToDatabase(userId, userData, dialog);
        }
    }

    private void saveProfileToDatabase(String userId, Map<String, Object> userData, AlertDialog dialog) {
        databaseReference.child(userId).setValue(userData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Volunteer.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(Volunteer.this, MyProfileActivity.class));
        }else if (id == R.id.nav_help) {
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