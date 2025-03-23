package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.OrgInventoryModel;
import com.kenyaredcross.domain_model.SuppliedGoodsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuppliedGoodsAdapter extends RecyclerView.Adapter<SuppliedGoodsAdapter.ViewHolder> {
    private final List<SuppliedGoodsModel> suppliedGoodsList = new ArrayList<>();
    private final DatabaseReference suppliedGoodsRef;
    private final DatabaseReference financeRequestsRef;
    private final DatabaseReference organisationInventoryRef;

    public SuppliedGoodsAdapter() {
        // Initialize Firebase references
        suppliedGoodsRef = FirebaseDatabase.getInstance().getReference("SuppliedGoods");
        financeRequestsRef = FirebaseDatabase.getInstance().getReference("FinanceRequests");
        organisationInventoryRef = FirebaseDatabase.getInstance().getReference("OrganisationInventory");

        // Load data from the SuppliedGoods node where status is "pending"
        loadSuppliedGoodsData();
    }

    private void loadSuppliedGoodsData() {
        suppliedGoodsRef.orderByChild("status").equalTo("pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                suppliedGoodsList.clear();  // Clear list before adding updated data
                for (DataSnapshot goodsSnapshot : snapshot.getChildren()) {
                    SuppliedGoodsModel goods = goodsSnapshot.getValue(SuppliedGoodsModel.class);
                    if (goods != null) {
                        goods.setRequestId(goodsSnapshot.getKey()); // Set the requestId from the snapshot key
                        suppliedGoodsList.add(goods);
                    }
                }
                notifyDataSetChanged();  // Notify the adapter to update the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(null, "Failed to load supplied goods: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.supplied_goods_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuppliedGoodsModel goods = suppliedGoodsList.get(position);
        holder.itemName.setText(goods.getItemName());
        holder.category.setText(goods.getCategory());
        holder.inventoryManager.setText(goods.getInventoryManager());
        holder.dateTime.setText(goods.getDateTime());
        holder.count.setText(String.valueOf(goods.getRequestCount()));
        holder.status.setText(goods.getStatus());
        holder.amount.setText(String.valueOf(goods.getTotalAmount()));

        // Disable button and avoid any action if status is already "approved"
        if ("approved".equals(goods.getStatus())) {
            holder.btnApprove.setEnabled(false);  // Disable the button
        } else {
            holder.btnApprove.setEnabled(true);  // Enable the button for non-approved items
            holder.btnApprove.setOnClickListener(v -> approveRequest(goods, holder));
        }

        holder.btnReject.setOnClickListener(v -> {
            // Add logic to reject the request, e.g., update the status in Firebase
            suppliedGoodsRef.child(goods.getRequestId()).child("status").setValue("rejected")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            holder.status.setText("rejected");
                            holder.btnApprove.setEnabled(false);  // Disable the button after rejection
                            Toast.makeText(holder.itemView.getContext(), "Supplied goods rejected", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Failed to reject request", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return suppliedGoodsList.size();
    }

    private void approveRequest(SuppliedGoodsModel goods, ViewHolder holder) {
        // Update the status to "approved" in the existing SuppliedGoods node
        suppliedGoodsRef.child(goods.getRequestId()).child("status").setValue("approved")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Fetch the current count of the item from OrganisationInventory
                        organisationInventoryRef.child(goods.getItemName()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                OrgInventoryModel orgInventoryItem = snapshot.getValue(OrgInventoryModel.class);
                                if (orgInventoryItem != null) {
                                    int currentCount = orgInventoryItem.getCount();
                                    int newCount = currentCount + goods.getRequestCount();

                                    // Update the OrganisationInventory with the new count
                                    organisationInventoryRef.child(goods.getItemName()).child("count").setValue(newCount)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    // Create a new FinanceRequest object with all details from SuppliedGoods
                                                    Map<String, Object> financeRequestData = new HashMap<>();
                                                    financeRequestData.put("category", goods.getCategory());
                                                    financeRequestData.put("inventoryManager", goods.getInventoryManager());
                                                    financeRequestData.put("itemName", goods.getItemName());
                                                    financeRequestData.put("requestCount", goods.getRequestCount());
                                                    financeRequestData.put("requestId", goods.getRequestId());
                                                    financeRequestData.put("status", "pending");  // Set status to pending
                                                    financeRequestData.put("supplier", goods.getSupplier());
                                                    financeRequestData.put("timestamp", goods.getDateTime());
                                                    financeRequestData.put("totalAmount", goods.getTotalAmount());

                                                    // Store the finance request in Firebase
                                                    financeRequestsRef.push().setValue(financeRequestData)
                                                            .addOnCompleteListener(financeTask -> {
                                                                if (financeTask.isSuccessful()) {
                                                                    holder.status.setText("approved");
                                                                    holder.btnApprove.setEnabled(false);  // Disable the button after approval
                                                                    Toast.makeText(holder.itemView.getContext(), "Supplied goods approved and finance request created", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(holder.itemView.getContext(), "Failed to create finance request", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(holder.itemView.getContext(), "Failed to update OrganisationInventory", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(holder.itemView.getContext(), "Item not found in OrganisationInventory", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(holder.itemView.getContext(), "Failed to fetch OrganisationInventory data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, category, inventoryManager, dateTime, count, status, amount;
        Button btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.RQitemName);
            category = itemView.findViewById(R.id.RQitemCategory);
            inventoryManager = itemView.findViewById(R.id.inventoryMangerRQ);
            dateTime = itemView.findViewById(R.id.dateTimeRQ);
            count = itemView.findViewById(R.id.countRQ);
            status = itemView.findViewById(R.id.statusRQ);
            btnApprove = itemView.findViewById(R.id.btnRQApprove);
            btnReject = itemView.findViewById(R.id.btnRQReject);
            amount = itemView.findViewById(R.id.amountRQ);
        }
    }
}