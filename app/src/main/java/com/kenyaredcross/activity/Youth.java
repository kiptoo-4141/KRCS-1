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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class Youth extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private CardView courses, youthDonationCard, myCourses, events, messaging, receipt, cert, attendanceCard;
    private Toolbar toolbar;

    private static final int DOCUMENT_PICK_CODE = 1001;
    private List<Uri> documentUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youth);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize UI components
        initializeUI();

        // Show profile popup when the user first logs in
        showProfilePopup();
    }

    private void initializeUI() {
        cert = findViewById(R.id.youthCertificationCard);
        cert.setOnClickListener(v -> startActivity(new Intent(Youth.this, CertActivity.class)));

        messaging = findViewById(R.id.youthMessagingCard);
        messaging.setOnClickListener(v -> startActivity(new Intent(Youth.this, FeedbacksActivity.class)));

        receipt = findViewById(R.id.youthReceiptCards);
        receipt.setOnClickListener(v -> startActivity(new Intent(Youth.this, YTReceiptsActivity.class)));

        events = findViewById(R.id.events);
        events.setOnClickListener(v -> startActivity(new Intent(Youth.this, EventsActivity.class)));

        myCourses = findViewById(R.id.mycourses);
        myCourses.setOnClickListener(v -> startActivity(new Intent(Youth.this, MyCoursesActivity.class)));

        attendanceCard = findViewById(R.id.classAttendanceCard);
        attendanceCard.setOnClickListener(v -> startActivity(new Intent(Youth.this, ClassAttendanceActivity.class)));

        courses = findViewById(R.id.courses);
        courses.setOnClickListener(v -> startActivity(new Intent(Youth.this, CoursesActivity.class)));

        youthDonationCard = findViewById(R.id.youthDonationCard);
        youthDonationCard.setOnClickListener(v -> startActivity(new Intent(Youth.this, DonationActivity.class)));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showProfilePopup() {
        String userId = user.getUid();

        databaseReference.child("Profiles").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showProfileDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error checking profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_update, null);
        builder.setView(dialogView);

        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        TextInputEditText etTown = dialogView.findViewById(R.id.etTown);
        TextInputEditText etIdNumber = dialogView.findViewById(R.id.etIdNumber);
        TextInputEditText etDob = dialogView.findViewById(R.id.etDob);
        Button btnSaveProfile = dialogView.findViewById(R.id.btnSaveProfile);
        Button btnUploadDocs = dialogView.findViewById(R.id.btnUploadDocs);
        RecyclerView rvDocuments = dialogView.findViewById(R.id.rvDocuments);

        // Auto-populate email and retrieve full name from Users node
        etEmail.setText(user.getEmail());
        etEmail.setEnabled(false);

        // Retrieve full name from Users node
        databaseReference.child("Users").child(user.getEmail().replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("username").getValue(String.class);
                    etUsername.setText(fullName);
                    etUsername.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error retrieving username: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up date picker for DOB
        etDob.setOnClickListener(v -> showDatePicker(etDob));

        // Set up document upload
        btnUploadDocs.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, DOCUMENT_PICK_CODE);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        btnSaveProfile.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String town = etTown.getText().toString().trim();
            String idNumber = etIdNumber.getText().toString().trim();
            String dob = etDob.getText().toString().trim();

            if (validateInputs(phone, town, idNumber, dob)) {
                checkUniquenessAndSave(phone, idNumber, town, dob, alertDialog);
            }
        });

        alertDialog.show();
    }

    private void showDatePicker(TextInputEditText etDob) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            if (selectedDate.after(Calendar.getInstance())) {
                Toast.makeText(Youth.this, "Date of Birth cannot be in the future", Toast.LENGTH_SHORT).show();
            } else {
                etDob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private boolean validateInputs(String phone, String town, String idNumber, String dob) {
        if (phone.isEmpty() || town.isEmpty() || idNumber.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkUniquenessAndSave(String phone, String idNumber, String town, String dob, AlertDialog dialog) {
        DatabaseReference profilesRef = databaseReference.child("Profiles");

        profilesRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Youth.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                } else {
                    profilesRef.orderByChild("idNumber").equalTo(idNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(Youth.this, "ID number already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                saveProfile(phone, town, idNumber, dob, dialog);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Youth.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Youth.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile(String phone, String town, String idNumber, String dob, AlertDialog dialog) {
        String userId = user.getUid();
        Map<String, Object> profile = new HashMap<>();
        profile.put("email", user.getEmail());
        profile.put("username", user.getDisplayName());
        profile.put("phone", phone);
        profile.put("town", town);
        profile.put("idNumber", idNumber);
        profile.put("dob", dob);

        // Upload documents to Firestore and store links
        if (!documentUris.isEmpty()) {
            List<String> documentUrls = new ArrayList<>();
            for (Uri uri : documentUris) {
                StorageReference fileRef = storageReference.child("documents/" + System.currentTimeMillis() + "_" + uri.getLastPathSegment());
                UploadTask uploadTask = fileRef.putFile(uri);
                uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    documentUrls.add(uri1.toString());
                    if (documentUrls.size() == documentUris.size()) {
                        profile.put("documents", documentUrls);
                        saveProfileToDatabase(profile, dialog);
                    }
                })).addOnFailureListener(e -> Toast.makeText(Youth.this, "Failed to upload document: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } else {
            saveProfileToDatabase(profile, dialog);
        }
    }

    private void saveProfileToDatabase(Map<String, Object> profile, AlertDialog dialog) {
        String userId = user.getUid();
        databaseReference.child("Profiles").child(userId).setValue(profile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Youth.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(Youth.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DOCUMENT_PICK_CODE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    documentUris.add(uri);
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                documentUris.add(uri);
            }
            Toast.makeText(this, "Documents selected: " + documentUris.size(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us2) {
            startActivity(new Intent(Youth.this, AboutUsActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(Youth.this, ContactUsActivity.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(Youth.this, HelpActivity.class));
        } else if (id == R.id.nav_log_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Youth.this, Login.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}