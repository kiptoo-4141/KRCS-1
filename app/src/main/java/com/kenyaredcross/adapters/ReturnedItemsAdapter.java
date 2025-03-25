package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.ReturnedItem;

import java.util.List;

public class ReturnedItemsAdapter extends RecyclerView.Adapter<ReturnedItemsAdapter.ViewHolder> {

    private final List<ReturnedItem> returnedItems;
    private final OnApproveClickListener approveClickListener;

    public ReturnedItemsAdapter(List<ReturnedItem> returnedItems, OnApproveClickListener approveClickListener) {
        this.returnedItems = returnedItems;
        this.approveClickListener = approveClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_returned_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReturnedItem item = returnedItems.get(position);

        holder.itemNameTextView.setText(item.getItemName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.statusTextView.setText(item.getStatus());
        holder.sourceTextView.setText(item.getSource());

        // Show/hide approve button based on status
        if ("return approved".equalsIgnoreCase(item.getStatus())) {
            holder.approveButton.setVisibility(View.GONE);
        } else {
            holder.approveButton.setVisibility(View.VISIBLE);
            holder.approveButton.setOnClickListener(v -> {
                if (approveClickListener != null) {
                    approveClickListener.onApproveClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return returnedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, quantityTextView, statusTextView, sourceTextView;
        Button approveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            sourceTextView = itemView.findViewById(R.id.sourceTextView);
            approveButton = itemView.findViewById(R.id.approveButton);
        }
    }

    public interface OnApproveClickListener {
        void onApproveClick(ReturnedItem returnedItem);
    }
}