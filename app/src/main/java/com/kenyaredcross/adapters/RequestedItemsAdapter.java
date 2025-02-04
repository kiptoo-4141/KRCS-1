package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.RequestedItem;

import java.util.List;

public class RequestedItemsAdapter extends RecyclerView.Adapter<RequestedItemsAdapter.ViewHolder> {

    private final List<RequestedItem> itemList;

    public RequestedItemsAdapter(List<RequestedItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_requested, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestedItem item = itemList.get(position);
        holder.itemName.setText("Item: " + item.getItemName());
        holder.quantity.setText("Quantity: " + item.getQuantity());
        holder.status.setText("Status: " + item.getStatus());
        holder.trainerEmail.setText("Requested by: " + item.getTrainerEmail());

        if (item.getStatus().equals("Pending")) {
            holder.approveButton.setVisibility(View.VISIBLE);
        } else {
            holder.approveButton.setVisibility(View.GONE);
        }

        holder.approveButton.setOnClickListener(v -> {
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("ItemRequests").child(item.getRequestId());
            itemRef.child("status").setValue("Approved")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(v.getContext(), "Item Approved!", Toast.LENGTH_SHORT).show();
                        item.setStatus("Approved");
                        notifyItemChanged(position);
                    })
                    .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Approval Failed!", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, quantity, status, trainerEmail;
        Button approveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.quantity);
            status = itemView.findViewById(R.id.status);
            trainerEmail = itemView.findViewById(R.id.trainerEmail);
            approveButton = itemView.findViewById(R.id.approveButton);
        }
    }
}
