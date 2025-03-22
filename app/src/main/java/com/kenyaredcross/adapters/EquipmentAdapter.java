package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.EquipmentModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EquipmentAdapter extends FirebaseRecyclerAdapter<EquipmentModel, EquipmentAdapter.EquipmentViewHolder> {

    private Context context;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    public EquipmentAdapter(@NonNull FirebaseRecyclerOptions<EquipmentModel> options, Context context) {
        super(options);
        this.context = context;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position, @NonNull EquipmentModel model) {
        holder.itemName.setText(model.getItemName());
        holder.category.setText(model.getCategory());
        holder.count.setText(String.valueOf(model.getCount()));

        holder.borrowButton.setOnClickListener(v -> {
            if (user != null) {
                String userEmailKey = user.getEmail().replace(".", "_");
                databaseReference.child("Users").child(userEmailKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String username = snapshot.child("username").getValue(String.class);
                            String email = user.getEmail();
                            String itemName = model.getItemName();
                            String category = model.getCategory();
                            int count = model.getCount();

                            // Show dialog to enter the number of items to borrow
                            showBorrowDialog(context, username, email, itemName, category, count);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBorrowDialog(Context context, String username, String email, String itemName, String category, int availableCount) {
        // Implement a dialog to allow the user to enter the number of items to borrow
        // Ensure the borrowed count does not exceed the available count
        // Save the borrowed details to the `BorrowedEquipments` node
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipment, parent, false);
        return new EquipmentViewHolder(view);
    }

    static class EquipmentViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, category, count;
        Button borrowButton;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            category = itemView.findViewById(R.id.category);
            count = itemView.findViewById(R.id.count);
            borrowButton = itemView.findViewById(R.id.borrowButton);
        }
    }
}