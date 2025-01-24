package com.kenyaredcross.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.SupplyPaymentModel;
import java.util.ArrayList;
import java.util.List;

public class SupplyPaymentAdapter extends RecyclerView.Adapter<SupplyPaymentAdapter.SupplyPaymentViewHolder> {
    private final Context context;
    private final List<SupplyPaymentModel> supplyPaymentList = new ArrayList<>();

    public SupplyPaymentAdapter(Context context) {
        this.context = context;
        fetchPendingPaymentsFromDatabase();
    }

    private void fetchPendingPaymentsFromDatabase() {
        DatabaseReference paymentsRef = FirebaseDatabase.getInstance().getReference("Payments");
        paymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                supplyPaymentList.clear();
                for (DataSnapshot paymentSnapshot : dataSnapshot.getChildren()) {
                    SupplyPaymentModel payment = paymentSnapshot.getValue(SupplyPaymentModel.class);
                    if (payment != null && "pending".equals(payment.getStatus())) {
                        payment.setId(paymentSnapshot.getKey());  // Set the ID from Firebase key
                        supplyPaymentList.add(payment);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    @NonNull
    @Override
    public SupplyPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.supply_payment_item, parent, false);
        return new SupplyPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplyPaymentViewHolder holder, int position) {
        SupplyPaymentModel payment = supplyPaymentList.get(position);
        holder.itemNameTextView.setText(payment.getItemName());
        holder.itemCategoryFN.setText(payment.getCategory());
        holder.dateTime.setText(payment.getDateTime());
        holder.inventoryManagerName.setText(payment.getInventoryManager());
        holder.rqstcount.setText(String.valueOf(payment.getRequestCount()));
        holder.amountTextView.setText("Kshs " + payment.getAmount());
        holder.statusTextView.setText(payment.getStatus());

        // Set up button actions
        holder.approveButton.setOnClickListener(v -> updateStatus(payment, "approved"));
        holder.rejectButton.setOnClickListener(v -> updateStatus(payment, "rejected"));
    }

    private void updateStatus(SupplyPaymentModel payment, String status) {
        if (payment.getId() != null) {  // Ensure ID is not null
            DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("Payments").child(payment.getId());
            paymentRef.child("status").setValue(status);

            // Create a report in the SupplyReports node
            DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("SupplyReports").push();
            payment.setStatus(status); // Update status in payment before saving to reports
            reportsRef.setValue(payment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Report created successfully
                        } else {
                            // Handle the error
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return supplyPaymentList.size();
    }

    public static class SupplyPaymentViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemCategoryFN, dateTime, inventoryManagerName, rqstcount, amountTextView, statusTextView;
        Button approveButton, rejectButton;

        public SupplyPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemCategoryFN = itemView.findViewById(R.id.itemCategoryFN);
            dateTime = itemView.findViewById(R.id.dateTime);
            inventoryManagerName = itemView.findViewById(R.id.inventoryManagerName);
            rqstcount = itemView.findViewById(R.id.rqstcount);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
