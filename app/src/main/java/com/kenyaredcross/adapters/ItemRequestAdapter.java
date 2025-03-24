package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.ItemRequestModel;

import java.util.List;

public class ItemRequestAdapter extends RecyclerView.Adapter<ItemRequestAdapter.ViewHolder> {

    private final List<ItemRequestModel> itemRequestList;
    private final OnReturnButtonClickListener returnButtonClickListener;

    public ItemRequestAdapter(List<ItemRequestModel> itemRequestList,
                              OnReturnButtonClickListener returnButtonClickListener) {
        this.itemRequestList = itemRequestList;
        this.returnButtonClickListener = returnButtonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_use, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position >= itemRequestList.size()) {
            return; // Prevent IndexOutOfBoundsException
        }

        ItemRequestModel itemRequest = itemRequestList.get(position);
        if (itemRequest == null) {
            return; // Skip null items
        }

        // Set item name
        holder.itemNameTextView.setText(itemRequest.getItemName() != null ?
                itemRequest.getItemName() : "N/A");

        // Set quantity
        holder.quantityTextView.setText(String.valueOf(itemRequest.getQuantity()));

        // Set status with proper null check
        String status = itemRequest.getStatus();
        holder.statusTextView.setText(status != null ? status : "N/A");

        // Handle return button visibility and click
        if (holder.returnButton != null) {
            if (status != null && status.equalsIgnoreCase("returned")) {
                holder.returnButton.setVisibility(View.GONE);
            } else {
                holder.returnButton.setVisibility(View.VISIBLE);
                holder.returnButton.setOnClickListener(v -> {
                    if (returnButtonClickListener != null && itemRequest.getKey() != null) {
                        returnButtonClickListener.onReturnButtonClick(itemRequest);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemRequestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, quantityTextView, statusTextView;
        Button returnButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            returnButton = itemView.findViewById(R.id.returnButton);
        }
    }

    public interface OnReturnButtonClickListener {
        void onReturnButtonClick(ItemRequestModel itemRequest);
    }
}