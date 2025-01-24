package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.OrgInventoryModel;

import java.util.ArrayList;
import java.util.List;

public class OrgInventoryAdapter extends RecyclerView.Adapter<OrgInventoryAdapter.OrgInventoryViewHolder> {

    private List<OrgInventoryModel> inventoryList;

    // Constructor
    public OrgInventoryAdapter() {
        this.inventoryList = new ArrayList<>();
    }

    // Method to set the inventory list
    public void setInventoryList(List<OrgInventoryModel> inventoryList) {
        this.inventoryList = inventoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrgInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organisation_inventory_item, parent, false);
        return new OrgInventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgInventoryViewHolder holder, int position) {
        OrgInventoryModel model = inventoryList.get(position);
        holder.itemNameTextView.setText(model.getItemName());
        holder.itemCategoryTextView.setText(model.getCategory());
        holder.itemCountTextView.setText(String.valueOf(model.getCount()));
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    // ViewHolder class
    static class OrgInventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemCategoryTextView, itemCountTextView;

        OrgInventoryViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemCategoryTextView = itemView.findViewById(R.id.itemCategoryTextView);
            itemCountTextView = itemView.findViewById(R.id.itemCountTextView);
        }
    }
}
