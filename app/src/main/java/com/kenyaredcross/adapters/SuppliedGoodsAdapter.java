package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.domain_model.SuppliedGoodsModel;
import com.kenyaredcross.R;

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
                        suppliedGoodsList.add(goods);
                    }
                }
                notifyDataSetChanged();  // Notify the adapter to update the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
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
        holder.amount.setText(String.valueOf(goods.getAmount()));

        // Disable button and avoid any action if status is already "approved"
        if ("approved".equals(goods.getStatus())) {
            holder.btnApprove.setEnabled(false);  // Disable the button
        } else {
            holder.btnApprove.setEnabled(true);  // Enable the button for non-approved items
            holder.btnApprove.setOnClickListener(v -> {
                // Create a new FinanceRequest object
                Map<String, Object> financeRequestData = new HashMap<>();
                financeRequestData.put("itemName", goods.getItemName());
                financeRequestData.put("category", goods.getCategory());
                financeRequestData.put("inventoryManager", goods.getInventoryManager());
                financeRequestData.put("dateTime", goods.getDateTime());
                financeRequestData.put("requestCount", goods.getRequestCount());
                financeRequestData.put("amount", goods.getAmount());
                financeRequestData.put("status", "pending");

                // Store the finance request in Firebase
                financeRequestsRef.push().setValue(financeRequestData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Update OrganisationInventory if the item exists
                                updateOrganisationInventory(goods.getItemName(), goods.getCategory(), goods.getRequestCount());

                                // Update the status to "approved" in SuppliedGoods
                                suppliedGoodsRef.child(goods.getRequestId()).child("status").setValue("approved");
                                holder.status.setText("approved");
                                holder.btnApprove.setEnabled(false);  // Disable the button after approval
                            }
                        });
            });
        }

        holder.btnReject.setOnClickListener(v -> {
            // Add logic to reject the request, e.g., update the status in Firebase
        });
    }

    @Override
    public int getItemCount() {
        return suppliedGoodsList.size();
    }

    private void updateOrganisationInventory(String itemName, String category, int requestCount) {
        organisationInventoryRef.child(itemName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().exists()) {
                    int existingCount = task.getResult().child("count").getValue(Integer.class);
                    int newCount = existingCount + requestCount;

                    organisationInventoryRef.child(itemName).child("count").setValue(newCount);
                } else {
                    Map<String, Object> newItemData = new HashMap<>();
                    newItemData.put("itemName", itemName);
                    newItemData.put("category", category);
                    newItemData.put("count", requestCount);

                    organisationInventoryRef.child(itemName).setValue(newItemData);
                }
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
