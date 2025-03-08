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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.SupplyRequestModel;

public class SupplyRequestAdapter extends FirebaseRecyclerAdapter<SupplyRequestModel, SupplyRequestAdapter.SupplyRequestViewHolder> {

    public SupplyRequestAdapter(@NonNull FirebaseRecyclerOptions<SupplyRequestModel> options) {
        super(options);
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
        requestRef.child("status").setValue("approved")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(holder.itemView.getContext(), "Request approved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to approve request", Toast.LENGTH_SHORT).show();
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