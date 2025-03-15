package com.kenyaredcross.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;

public class MyProfileActivity extends AppCompatActivity {

    private TextView username, email, dob, idNumber, phone, location, town, specialization;
    private LinearLayout documentsLayout;
    private DatabaseReference usersReference, profilesReference;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);



        // Apply fade-in animation to the ID card
        CardView idCard = findViewById(R.id.idCard);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        idCard.startAnimation(fadeIn);

        // Initialize Firebase Auth and Database
        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        profilesReference = FirebaseDatabase.getInstance().getReference("Profiles");

        // Initialize TextViews and Layout
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        dob = findViewById(R.id.dob);
        idNumber = findViewById(R.id.idNumber);
        phone = findViewById(R.id.phone);
        location = findViewById(R.id.location);
        town = findViewById(R.id.town);
        specialization = findViewById(R.id.specialization);
        documentsLayout = findViewById(R.id.documentsLayout);

        // Fetch and display user data
        fetchUserData();
    }

    private void fetchUserData() {
        String userEmail = firebaseAuth.getCurrentUser().getEmail();
        if (userEmail == null) return;

        // Replace '.' with '_' to match Firebase key format
        String emailKey = userEmail.replace(".", "_");

        // Fetch user role and username from Users node
        usersReference.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userRole = dataSnapshot.child("role").getValue(String.class);
                    String userUsername = dataSnapshot.child("username").getValue(String.class);
                    username.setText(userUsername != null ? userUsername : "Not Provided");

                    // Fetch profile data from Profiles node based on role
                    fetchProfileData(userRole);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void fetchProfileData(String userRole) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        profilesReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve common data
                    String userEmail = dataSnapshot.child("email").getValue(String.class);
                    String userDob = dataSnapshot.child("dob").getValue(String.class);
                    String userIdNumber = dataSnapshot.child("idNumber").getValue(String.class);
                    String userPhone = dataSnapshot.child("phone").getValue(String.class);

                    // Update common fields
                    email.setText(userEmail != null ? userEmail : "Not Provided");
                    dob.setText(userDob != null ? userDob : "Not Provided");
                    idNumber.setText(userIdNumber != null ? userIdNumber : "Not Provided");
                    phone.setText(userPhone != null ? userPhone : "Not Provided");

                    // Role-specific fields
                    if ("Youth".equals(userRole)) {
                        String userTown = dataSnapshot.child("town").getValue(String.class);
                        town.setText(userTown != null ? userTown : "Not Provided");
                        location.setVisibility(View.GONE);
                        specialization.setVisibility(View.GONE);
                    } else if ("Volunteer".equals(userRole)) {
                        String userLocation = dataSnapshot.child("location").getValue(String.class);
                        String userSpecialization = dataSnapshot.child("specialization").getValue(String.class);
                        location.setText(userLocation != null ? userLocation : "Not Provided");
                        specialization.setText(userSpecialization != null ? userSpecialization : "Not Provided");
                        town.setVisibility(View.GONE);
                    }

                    // Fetch and display documents
                    if (dataSnapshot.hasChild("documents")) {
                        for (DataSnapshot documentSnapshot : dataSnapshot.child("documents").getChildren()) {
                            String documentUrl = documentSnapshot.getValue(String.class);
                            if (documentUrl != null) {
                                addDocumentLink(documentUrl);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void addDocumentLink(String documentUrl) {
        TextView documentLink = new TextView(this);
        documentLink.setText("Download Document");
        documentLink.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        documentLink.setPadding(0, 8, 0, 8);
        documentLink.setOnClickListener(v -> downloadDocument(documentUrl));
        documentsLayout.addView(documentLink);
    }

    private void downloadDocument(String documentUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(documentUrl));
        request.setTitle("Document Download");
        request.setDescription("Downloading document...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "document_" + System.currentTimeMillis() + ".pdf");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}