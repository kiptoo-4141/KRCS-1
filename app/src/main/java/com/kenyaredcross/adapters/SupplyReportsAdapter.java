package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.domain_model.SupplyReportsModel;
import com.kenyaredcross.R;

import java.util.List;

public class SupplyReportsAdapter extends RecyclerView.Adapter<SupplyReportsAdapter.ViewHolder> {
    private final List<SupplyReportsModel> supplyReportsList;

    public SupplyReportsAdapter(List<SupplyReportsModel> supplyReportsList) {
        this.supplyReportsList = supplyReportsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.supply_report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupplyReportsModel report = supplyReportsList.get(position);
        holder.itemNameTextView.setText(report.getItemName());
        holder.itemCategoryTextView.setText(report.getCategory());
        holder.requestCountTextView.setText(String.valueOf(report.getRequestCount()));
        holder.amountTextView.setText(String.valueOf(report.getAmount()));
        holder.dateTimeTextView.setText(report.getDateTime());
        holder.inventoryManagerTextView.setText(report.getInventoryManager());
        holder.statusTextView.setText(report.getStatus());
    }

    @Override
    public int getItemCount() {
        return supplyReportsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        TextView itemCategoryTextView;
        TextView requestCountTextView;
        TextView amountTextView;
        TextView dateTimeTextView;
        TextView inventoryManagerTextView;
        TextView statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemCategoryTextView = itemView.findViewById(R.id.itemCategoryTextView);
            requestCountTextView = itemView.findViewById(R.id.requestCountTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            inventoryManagerTextView = itemView.findViewById(R.id.inventoryManagerTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}