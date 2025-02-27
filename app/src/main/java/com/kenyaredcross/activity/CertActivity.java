package com.kenyaredcross.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CertActivity extends AppCompatActivity {

    private DatabaseReference enrollmentsRef;
    private FirebaseAuth auth;
    private LinearLayout certContainer;
    private Button downloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cert);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        certContainer = findViewById(R.id.certContainer);
        downloadBtn = findViewById(R.id.downloadBtn);

        String userEmail = auth.getCurrentUser().getEmail();
        if (userEmail != null) {
            String formattedEmail = userEmail.replace(".", "_");
            enrollmentsRef = FirebaseDatabase.getInstance().getReference("Enrollments").child(formattedEmail);

            fetchCertificates();
        }

        downloadBtn.setOnClickListener(v -> generatePDF());
    }

    private void fetchCertificates() {
        enrollmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                certContainer.removeAllViews();
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String title = courseSnapshot.child("title").getValue(String.class);
                    String duration = courseSnapshot.child("duration").getValue(String.class);
                    String certificationStatus = courseSnapshot.child("certificationStatus").getValue(String.class);
                    String imageUrl = courseSnapshot.child("image").getValue(String.class);
                    String status = courseSnapshot.child("status").getValue(String.class);

                    if ("completed".equals(status)) {
                        addCertificateView(title, duration, certificationStatus, imageUrl);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CertActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCertificateView(String title, String duration, String certificationStatus, String imageUrl) {
        View certView = getLayoutInflater().inflate(R.layout.cert_item, certContainer, false);

        TextView certTitle = certView.findViewById(R.id.certTitle);
        TextView certDuration = certView.findViewById(R.id.certDuration);
        TextView certStatus = certView.findViewById(R.id.certStatus);
        ImageView certImage = certView.findViewById(R.id.certImage);

        certTitle.setText("Course: " + title);
        certDuration.setText("Duration: " + duration);
        certStatus.setText("Certification: " + certificationStatus);

        Picasso.get().load(imageUrl).into(certImage);
        certContainer.addView(certView);
    }

    private void generatePDF() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        PdfDocument document = new PdfDocument();
        Bitmap bitmap = Bitmap.createBitmap(certContainer.getWidth(), certContainer.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        certContainer.draw(canvas);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas pdfCanvas = page.getCanvas();
        pdfCanvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "certificate.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            Toast.makeText(this, "Certificate downloaded successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
