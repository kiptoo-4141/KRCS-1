package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
import com.kenyaredcross.domain_model.SupplyRequestModel;

public class SupplyRequestAdapter extends FirebaseRecyclerAdapter<SupplyRequestModel, SupplyRequestAdapter.SupplyRequestViewHolder> {

    private final DatabaseReference supplierInventoryRef;

    public SupplyRequestAdapter(@NonNull FirebaseRecyclerOptions<SupplyRequestModel> options) {
        super(options);
        // Initialize Firebase reference for SupplierInventory
        supplierInventoryRef = FirebaseDatabase.getInstance().getReference("SupplierInventory");
    }

    @Override
    protected void onBindViewHolder(@NonNull SupplyRequestViewHolder holder, int position, @NonNull SupplyRequestModel model) {
        holder.itemName.setText(model.getItemName());
        holder.category.setText(model.getCategory());
        holder.inventoryManager.setText(model.getInventoryManager());
        holder.timestamp.setText(model.getTimestamp());
        holder.requestCount.setText(String.valueOf(model.getRequestCount()));
        holder.status.setText(model.getStatus());
        holder.totalAmount.setText(String.format("%.2f", model.getTotalAmount()));

        // Show approve button only for pending requests
        if ("pending".equals(model.getStatus())) {
            holder.approveButton.setVisibility(View.VISIBLE);
            holder.approveButton.setOnClickListener(v -> approveRequest(model, holder));
        } else {
            holder.approveButton.setVisibility(View.GONE);
        }
    }

    private void approveRequest(SupplyRequestModel model, SupplyRequestViewHolder holder) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("SupplyRequest").child(model.getRequestId());
        DatabaseReference suppliedGoodsRef = FirebaseDatabase.getInstance().getReference("SuppliedGoods").push(); // Create a new entry in SuppliedGoods

        // Copy all details from SupplyRequest to SuppliedGoods
        suppliedGoodsRef.child("requestId").setValue(model.getRequestId());
        suppliedGoodsRef.child("itemName").setValue(model.getItemName());
        suppliedGoodsRef.child("category").setValue(model.getCategory());
        suppliedGoodsRef.child("inventoryManager").setValue(model.getInventoryManager());
        suppliedGoodsRef.child("supplier").setValue(model.getSupplier());
        suppliedGoodsRef.child("requestCount").setValue(model.getRequestCount());
        suppliedGoodsRef.child("status").setValue("pending"); // Set default status to "pending"
        suppliedGoodsRef.child("timestamp").setValue(model.getTimestamp());
        suppliedGoodsRef.child("totalAmount").setValue(model.getTotalAmount());

        // Fetch the current stock count from SupplierInventory
        supplierInventoryRef.orderByChild("itemName").equalTo(model.getItemName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Integer currentStock = itemSnapshot.child("stock").getValue(Integer.class);
                        if (currentStock != null) {
                            int newStock = currentStock - model.getRequestCount();

                            // Update the stock count in SupplierInventory
                            itemSnapshot.getRef().child("stock").setValue(newStock)
                                    .addOnCompleteListener(stockUpdateTask -> {
                                        if (stockUpdateTask.isSuccessful()) {
                                            // Update the status in the SupplyRequest node to "approved"
                                            requestRef.child("status").setValue("approved")
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(holder.itemView.getContext(), "Request approved, stock updated, and moved to SuppliedGoods with pending status", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(holder.itemView.getContext(), "Failed to approve request", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(holder.itemView.getContext(), "Failed to update supplier stock", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Failed to fetch current stock", Toast.LENGTH_SHORT).show();
                        }
                        break; // Exit loop after finding the matching item
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Item not found in SupplierInventory", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(holder.itemView.getContext(), "Failed to fetch supplier inventory data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public SupplyRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supply_request_item, parent, false);
        return new SupplyRequestViewHolder(view);
    }

    static class SupplyRequestViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, category, inventoryManager, timestamp, requestCount, status, totalAmount;
        Button approveButton;

        public SupplyRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.RQitemName);
            category = itemView.findViewById(R.id.RQitemCategory);
            inventoryManager = itemView.findViewById(R.id.inventoryMangerRQ);
            timestamp = itemView.findViewById(R.id.timestampRQ);
            requestCount = itemView.findViewById(R.id.countRQ);
            status = itemView.findViewById(R.id.statusRQ);
            totalAmount = itemView.findViewById(R.id.totalAmountRQ);
            approveButton = itemView.findViewById(R.id.btnRQApprove);
        }
    }
}