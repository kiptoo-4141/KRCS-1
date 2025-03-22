package com.kenyaredcross.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.BorrowedEquipmentModel3;

import java.util.List;

public class BorrowedEquipmentAdapter3 extends RecyclerView.Adapter<BorrowedEquipmentAdapter3.ViewHolder> {

    private List<BorrowedEquipmentModel3> borrowedEquipmentList;
    private OnAssignButtonClickListener listener;

    public BorrowedEquipmentAdapter3(List<BorrowedEquipmentModel3> borrowedEquipmentList, OnAssignButtonClickListener listener) {
        this.borrowedEquipmentList = borrowedEquipmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrowed_equipment3, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowedEquipmentModel3 borrowedEquipment = borrowedEquipmentList.get(position);
        holder.itemNameTextView.setText(borrowedEquipment.getItemName());
        holder.countTextView.setText(String.valueOf(borrowedEquipment.getCount()));
        holder.statusTextView.setText(borrowedEquipment.getStatus());

        holder.assignButton.setOnClickListener(v -> listener.onAssignButtonClick(borrowedEquipment));
    }

    @Override
    public int getItemCount() {
        return borrowedEquipmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, countTextView, statusTextView;
        Button assignButton,returnbutton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            countTextView = itemView.findViewById(R.id.countTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            assignButton = itemView.findViewById(R.id.assignButton);
            returnbutton = itemView.findViewById(R.id.returnButton);
        }
    }

    public interface OnAssignButtonClickListener {
        void onAssignButtonClick(BorrowedEquipmentModel3 borrowedEquipment);
    }
}