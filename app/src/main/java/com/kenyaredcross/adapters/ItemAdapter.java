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
    private Context context;
    private List<Item> itemList;
    private String trainerEmail;

    public ItemAdapter(Context context, List<Item> itemList, String trainerEmail) {
        this.context = context;
        this.itemList = itemList;
        this.trainerEmail = trainerEmail;
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
        holder.itemName.setText(item.getItemName());
        holder.category.setText("Category: " + item.getCategory());
        holder.count.setText("Available: " + item.getCount());

        holder.requestButton.setOnClickListener(v -> {
            String quantityStr = holder.quantityInput.getText().toString();
            if (quantityStr.isEmpty()) {
                Toast.makeText(context, "Enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0 || quantity > item.getCount()) {
                Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference requestRef = FirebaseDatabase.getInstance()
                    .getReference("ItemRequests")
                    .push();
            ItemRequest request = new ItemRequest(item.getItemName(), quantity, trainerEmail);
            requestRef.setValue(request);

            Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show();
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
