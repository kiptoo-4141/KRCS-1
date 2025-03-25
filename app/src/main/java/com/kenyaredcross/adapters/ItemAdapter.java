package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Item;
import com.kenyaredcross.domain_model.ItemRequest;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final Context context;
    private final List<Item> itemList;
    private final String currentUserEmail;

    public ItemAdapter(Context context, List<Item> itemList, String currentUserEmail) {
        this.context = context;
        this.itemList = itemList;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.request_items_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        if (item == null) return;

        holder.itemName.setText(item.getItemName());
        holder.category.setText("Category: " + item.getCategory());
        holder.count.setText("Available: " + item.getCount());

        holder.requestButton.setOnClickListener(v -> {
            String quantityStr = holder.quantityInput.getText().toString().trim();
            if (quantityStr.isEmpty()) {
                Toast.makeText(context, "Please enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    Toast.makeText(context, "Quantity must be positive", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (quantity > item.getCount()) {
                    Toast.makeText(context, "Not enough items available", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference requestRef = FirebaseDatabase.getInstance()
                        .getReference("ItemRequests")
                        .push();

                ItemRequest request = new ItemRequest(
                        item.getItemName(),
                        quantity,
                        "Pending", // Default status
                        currentUserEmail
                );

                requestRef.setValue(request)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show();
                            holder.quantityInput.setText(""); // Clear input after successful request
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to send request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid quantity format", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, category, count;
        EditText quantityInput;
        Button requestButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            category = itemView.findViewById(R.id.category);
            count = itemView.findViewById(R.id.count);
            quantityInput = itemView.findViewById(R.id.quantity_input);
            requestButton = itemView.findViewById(R.id.request_button);
        }
    }
}