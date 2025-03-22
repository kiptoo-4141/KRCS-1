package com.kenyaredcross.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.BorrowedEquipmentModel2;

import java.util.List;

public class BorrowedEquipmentAdapter extends RecyclerView.Adapter<BorrowedEquipmentAdapter.ViewHolder> {

    private List<BorrowedEquipmentModel2> borrowedEquipmentList;
    private OnApproveButtonClickListener listener;

    public BorrowedEquipmentAdapter(List<BorrowedEquipmentModel2> borrowedEquipmentList, OnApproveButtonClickListener listener) {
        this.borrowedEquipmentList = borrowedEquipmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrowed_equipment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowedEquipmentModel2 borrowedEquipment = borrowedEquipmentList.get(position);
        holder.usernameTextView.setText(borrowedEquipment.getUsername());
        holder.itemNameTextView.setText(borrowedEquipment.getItemName());
        holder.countTextView.setText(String.valueOf(borrowedEquipment.getCount()));
        holder.statusTextView.setText(borrowedEquipment.getStatus());

        if ("approved".equals(borrowedEquipment.getStatus())) {
            holder.approveButton.setEnabled(false);
        } else {
            holder.approveButton.setOnClickListener(v -> listener.onApproveButtonClick(borrowedEquipment));
        }
    }

    @Override
    public int getItemCount() {
        return borrowedEquipmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, itemNameTextView, countTextView, statusTextView;
        Button approveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            countTextView = itemView.findViewById(R.id.countTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            approveButton = itemView.findViewById(R.id.approveButton);
        }
    }

    public interface OnApproveButtonClickListener {
        void onApproveButtonClick(BorrowedEquipmentModel2 borrowedEquipment);
    }
}