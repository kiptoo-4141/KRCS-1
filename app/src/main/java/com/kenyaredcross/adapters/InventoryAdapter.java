package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.InventoryModel;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryModel> inventoryList;

    public InventoryAdapter(List<InventoryModel> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryModel model = inventoryList.get(position);
        holder.itemName.setText(model.getItemName());
        holder.category.setText(model.getCategory());
        holder.count.setText(String.valueOf(model.getCount()));
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, category, count;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextView);
            category = itemView.findViewById(R.id.categoryTextView);
            count = itemView.findViewById(R.id.countTextView);
        }
    }
}
