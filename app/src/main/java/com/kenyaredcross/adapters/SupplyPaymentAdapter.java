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
    private final String supplierEmail;

    public SupplyPaymentAdapter(Context context, String supplierEmail) {
        this.context = context;
        this.supplierEmail = supplierEmail;
        fetchPaidRequestsForSupplier();
    }

    private void fetchPaidRequestsForSupplier() {
        DatabaseReference paidRequestsRef = FirebaseDatabase.getInstance().getReference("PaidRequests");
        paidRequestsRef.orderByChild("supplier").equalTo(supplierEmail)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        supplyPaymentList.clear();
                        for (DataSnapshot paymentSnapshot : dataSnapshot.getChildren()) {
                            SupplyPaymentModel payment = paymentSnapshot.getValue(SupplyPaymentModel.class);
                            if (payment != null && "approved".equals(payment.getStatus())) {
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

        // Disable buttons since these are already approved payments
        holder.approveButton.setVisibility(View.GONE);
        holder.rejectButton.setVisibility(View.GONE);
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