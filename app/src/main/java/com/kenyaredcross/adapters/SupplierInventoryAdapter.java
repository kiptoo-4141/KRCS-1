package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.SupplierInventoryModel;

import java.util.ArrayList;
import java.util.List;

public class SupplierInventoryAdapter extends FirebaseRecyclerAdapter<SupplierInventoryModel, SupplierInventoryAdapter.MyViewHolder> {

    private final DatabaseReference categoriesRef;
    private final List<String> categoriesList = new ArrayList<>();  // Holds the categories from Firebase

    public SupplierInventoryAdapter(@NonNull FirebaseRecyclerOptions<SupplierInventoryModel> options) {
        super(options);

        // Reference to the SupplierInventory node in Firebase
        categoriesRef = FirebaseDatabase.getInstance().getReference("SupplierInventory");
        fetchCategoriesFromFirebase();
    }

    // Fetches the categories from Firebase
    private void fetchCategoriesFromFirebase() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String category = categorySnapshot.child("category").getValue(String.class);
                    if (category != null && !categoriesList.contains(category)) {
                        categoriesList.add(category);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull SupplierInventoryModel model) {
        holder.itemName.setText(model.getItemName());
        holder.stock.setText("In Stock: " + model.getStock());
        holder.restockDate.setText("Last Restocked: " + model.getLastRestocked());

        // Set up the Spinner adapter with the retrieved categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemCategory.getContext(),
                android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.itemCategory.setAdapter(adapter);

        // Ensure the spinner shows the correct category for the current item
        holder.itemCategory.setSelection(getCategoryPosition(holder.itemCategory, model.getCategory()));

        // Handle edit and delete buttons
        holder.btnEdit.setOnClickListener(v -> {
            // Code for editing the item
        });

        holder.btnDelete.setOnClickListener(v -> {
            // Code for deleting the item
            getSnapshots().getSnapshot(position).getRef().removeValue();
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplier_inventory_view, parent, false);
        return new MyViewHolder(view);
    }

    // ViewHolder class to hold UI components
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, stock, restockDate;
        Spinner itemCategory;
        Button btnEdit, btnDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            stock = itemView.findViewById(R.id.inStockCount);
            restockDate = itemView.findViewById(R.id.restockedDate);
            itemCategory = itemView.findViewById(R.id.supplyCategorySpinner);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Helper function to select the correct category in the spinner
    private int getCategoryPosition(Spinner spinner, String category) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(category)) {
                return i;
            }
        }
        return 0;  // Default selection if no match found
    }
}
