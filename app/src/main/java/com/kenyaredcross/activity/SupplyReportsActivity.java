package com.kenyaredcross.activity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.kenyaredcross.R;
import com.kenyaredcross.adapters.SupplyReportsAdapter;
import com.kenyaredcross.domain_model.SupplyReportsModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SupplyReportsActivity extends AppCompatActivity {
    private RecyclerView supplyReportsView;
    private SupplyReportsAdapter supplyReportsAdapter;
    private List<SupplyReportsModel> supplyReportsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_reports);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        supplyReportsView = findViewById(R.id.SupplyReportsView);
        supplyReportsView.setLayoutManager(new LinearLayoutManager(this));
        supplyReportsList = new ArrayList<>();
        supplyReportsAdapter = new SupplyReportsAdapter(supplyReportsList);
        supplyReportsView.setAdapter(supplyReportsAdapter);

        // Get the logged-in supplier's email
        String currentUserEmail = mAuth.getCurrentUser().getEmail();

        // Fetch data from SuppliedGoods node
        fetchSuppliedGoodsData(currentUserEmail);

        // Set up download button
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(v -> exportToPDF(supplyReportsList));
    }

    private void fetchSuppliedGoodsData(String supplierEmail) {
        DatabaseReference suppliedGoodsRef = mDatabase.child("SuppliedGoods");
        suppliedGoodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                supplyReportsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the entry has a "supplier" field and matches the logged-in supplier's email
                    if (snapshot.hasChild("supplier")) {
                        String supplier = snapshot.child("supplier").getValue(String.class);
                        if (supplier != null && supplier.equals(supplierEmail)) {
                            // Create a SupplyReportsModel object from the snapshot
                            SupplyReportsModel report = new SupplyReportsModel(
                                    snapshot.getKey(), // ID
                                    snapshot.child("itemName").getValue(String.class), // Item Name
                                    snapshot.child("category").getValue(String.class), // Category
                                    snapshot.child("requestCount").getValue(Integer.class), // Request Count
                                    snapshot.child("totalAmount").getValue(Double.class), // Amount
                                    snapshot.child("timestamp").getValue(String.class), // Date & Time
                                    snapshot.child("inventoryManager").getValue(String.class), // Inventory Manager
                                    snapshot.child("status").getValue(String.class) // Status
                            );
                            supplyReportsList.add(report);
                        }
                    }
                }
                // Notify the adapter that the data has changed
                supplyReportsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SupplyReportsActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportToPDF(List<SupplyReportsModel> reports) {
        // Create a file in the Downloads directory
        String fileName = "SupplyReports_" + System.currentTimeMillis() + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        try {
            // Initialize PDF writer and document
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add a title to the PDF
            Paragraph title = new Paragraph("Supply Reports")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18);
            document.add(title);

            // Create a table for the data
            float[] columnWidths = {100, 100, 100, 100, 100, 100, 100};
            Table table = new Table(columnWidths);

            // Add table headers
            table.addHeaderCell("Item Name");
            table.addHeaderCell("Category");
            table.addHeaderCell("Request Count");
            table.addHeaderCell("Amount");
            table.addHeaderCell("Date & Time");
            table.addHeaderCell("Inventory Manager");
            table.addHeaderCell("Status");

            // Add data rows
            for (SupplyReportsModel report : reports) {
                table.addCell(report.getItemName());
                table.addCell(report.getCategory());
                table.addCell(String.valueOf(report.getRequestCount()));
                table.addCell(String.valueOf(report.getAmount()));
                table.addCell(report.getDateTime());
                table.addCell(report.getInventoryManager());
                table.addCell(report.getStatus());
            }

            // Add the table to the document
            document.add(table);

            // Close the document
            document.close();

            // Notify the user
            Toast.makeText(this, "PDF saved to Downloads: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Failed to create PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}