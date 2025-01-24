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

    public SupplyRequestAdapter(@NonNull FirebaseRecyclerOptions<SupplyRequestModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public SupplyRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supply_request_item, parent, false);
        return new SupplyRequestViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull SupplyRequestViewHolder holder, int position, @NonNull SupplyRequestModel model) {
        holder.itemName.setText(model.getItemName());
        holder.category.setText(model.getCategory());
        holder.inventoryManager.setText(model.getInventoryManager());
//        holder.timestamp.setText(model.getTimestamp());  // Display timestamp
        holder.requestCount.setText(String.valueOf(model.getRequestCount()));
        holder.status.setText(model.getStatus());

        holder.approveButton.setOnClickListener(view -> approveRequest(model, holder));
    }

    private void approveRequest(SupplyRequestModel model, SupplyRequestViewHolder holder) {
        DatabaseReference supplyRequestRef = FirebaseDatabase.getInstance().getReference("SupplyRequest").child(model.getRequestId());
        supplyRequestRef.child("status").setValue("approved").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateInventory(model, holder);
                createSuppliedGoodsEntry(model, holder);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Failed to update request status.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInventory(SupplyRequestModel model, SupplyRequestViewHolder holder) {
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("SupplierInventory");
        inventoryRef.orderByChild("itemName").equalTo(model.getItemName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot itemSnapshot = dataSnapshot.getChildren().iterator().next();
                    String itemId = itemSnapshot.getKey();
                    Integer stock = itemSnapshot.child("stock").getValue(Integer.class);

                    if (stock != null && stock >= model.getRequestCount()) {
                        int newStock = stock - model.getRequestCount();
                        inventoryRef.child(itemId).child("stock").setValue(newStock)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(holder.itemView.getContext(), "Inventory updated successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(holder.itemView.getContext(), "Failed to update inventory.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Insufficient stock for the requested amount.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Item not found in SupplierInventory.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.itemView.getContext(), "Failed to retrieve item information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createSuppliedGoodsEntry(SupplyRequestModel model, SupplyRequestViewHolder holder) {
        DatabaseReference suppliedGoodsRef = FirebaseDatabase.getInstance().getReference("SuppliedGoods").push();
        suppliedGoodsRef.child("amount").setValue(model.getTotalAmount()); // Assuming this should be the count
        suppliedGoodsRef.child("category").setValue(model.getCategory());
//        suppliedGoodsRef.child("timestamp").setValue(model.getTimestamp());
        suppliedGoodsRef.child("inventoryManager").setValue(model.getInventoryManager());
        suppliedGoodsRef.child("itemName").setValue(model.getItemName());
        suppliedGoodsRef.child("requestCount").setValue(model.getRequestCount());
        suppliedGoodsRef.child("requestId").setValue(model.getRequestId());
        suppliedGoodsRef.child("status").setValue("pending")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(holder.itemView.getContext(), "Supplied goods entry created successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to create supplied goods entry.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static class SupplyRequestViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, category, inventoryManager, timestamp, requestCount, status;
        Button approveButton;

        public SupplyRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.RQitemName);
            category = itemView.findViewById(R.id.RQitemCategory);
            inventoryManager = itemView.findViewById(R.id.inventoryMangerRQ);
//            timestamp = itemView.findViewById(R.id.timestampRQ);  // Updated to use timestamp
            requestCount = itemView.findViewById(R.id.countRQ);
            status = itemView.findViewById(R.id.statusRQ);
            approveButton = itemView.findViewById(R.id.btnRQApprove);
        }
    }
}
