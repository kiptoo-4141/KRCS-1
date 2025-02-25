package com.kenyaredcross.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.SupplierInventory;

import java.util.List;

public class SuppliersInventoryAdapter extends RecyclerView.Adapter<SuppliersInventoryAdapter.ViewHolder> {
    private final Context context;
    private final List<SupplierInventory> inventoryList;
    private final DatabaseReference databaseReference;

    public SuppliersInventoryAdapter(Context context, List<SupplierInventory> inventoryList) {
        this.context = context;
        this.inventoryList = inventoryList;
        databaseReference = FirebaseDatabase.getInstance().getReference("SupplierInventory");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupplierInventory item = inventoryList.get(position);

        holder.itemName.setText(item.getItemName());
        holder.category.setText("Category: " + item.getCategory());
        holder.stock.setText("Stock: " + item.getStock());
        holder.unitPrice.setText("Unit Price: " + item.getUnitPrice());

        holder.btnEditStock.setOnClickListener(v -> showUpdateDialog(item, "stock"));
        holder.btnEditPrice.setOnClickListener(v -> showUpdateDialog(item, "unitPrice"));
    }

    private void showUpdateDialog(SupplierInventory item, String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update " + field);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty()) {
                int updatedValue = Integer.parseInt(newValue);
                databaseReference.child(item.getItemId()).child(field).setValue(updatedValue)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, field + " updated successfully", Toast.LENGTH_SHORT).show();
                            if (field.equals("stock")) {
                                item.setStock(updatedValue);
                            } else {
                                item.setUnitPrice(updatedValue);
                            }
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show());
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, category, stock, unitPrice;
        Button btnEditStock, btnEditPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.txtItemName);
            category = itemView.findViewById(R.id.txtCategory);
            stock = itemView.findViewById(R.id.txtStock);
            unitPrice = itemView.findViewById(R.id.txtUnitPrice);
            btnEditStock = itemView.findViewById(R.id.btnEditStock);
            btnEditPrice = itemView.findViewById(R.id.btnEditPrice);
        }
    }
}
