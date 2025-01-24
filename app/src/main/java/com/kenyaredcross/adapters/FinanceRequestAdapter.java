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
import com.kenyaredcross.domain_model.FinanceRequestModel;
import com.kenyaredcross.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FinanceRequestAdapter extends RecyclerView.Adapter<FinanceRequestAdapter.FinanceRequestViewHolder> {
    private final List<FinanceRequestModel> requestList;
    private final Context context;
    private final DatabaseReference financeRequestsRef;
    private final DatabaseReference paymentsRef;
    private final DatabaseReference paidRequestsRef;
    private final DatabaseReference financeReportsRef;

    public FinanceRequestAdapter(Context context, List<FinanceRequestModel> requestList) {
        this.context = context;
        this.requestList = requestList;
        financeRequestsRef = FirebaseDatabase.getInstance().getReference("FinanceRequests");
        paymentsRef = FirebaseDatabase.getInstance().getReference("Payments");
        paidRequestsRef = FirebaseDatabase.getInstance().getReference("PaidRequests");
        financeReportsRef = FirebaseDatabase.getInstance().getReference("FinanceReports");

        loadFinanceRequests(); // Load requests from Firebase
    }

    private void loadFinanceRequests() {
        financeRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FinanceRequestModel request = dataSnapshot.getValue(FinanceRequestModel.class);
                    if (request != null) {
                        // Set the requestId using the Firebase key
                        request.setRequestId(dataSnapshot.getKey());

                        // Only add the request to the list if its status is "pending"
                        if ("pending".equals(request.getStatus())) {
                            requestList.add(request);
                        }
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });
    }


    @NonNull
    @Override
    public FinanceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.finance_request_item, parent, false);
        return new FinanceRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinanceRequestViewHolder holder, int position) {
        FinanceRequestModel request = requestList.get(position);

        // Bind data to the views
        holder.itemNameTextView.setText(request.getItemName());
        holder.itemCategoryFN.setText(request.getCategory());
        holder.dateTime.setText(request.getDateTime());
        holder.inventoryManagerName.setText(request.getInventoryManager());
        holder.rqstcount.setText(String.valueOf(request.getRequestCount()));
        holder.amountTextView.setText("Kshs " + request.getAmount());
        holder.statusTextView.setText(request.getStatus());

        // Set up button actions
        holder.approveButton.setOnClickListener(v -> approveRequest(request));
        holder.rejectButton.setOnClickListener(v -> rejectRequest(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private void approveRequest(FinanceRequestModel request) {
        if (request.getRequestId() != null) {
            // Check the current status of the request
            if (!"approved".equals(request.getStatus())) {
                // Update the request status to "approved"
                financeRequestsRef.child(request.getRequestId()).child("status").setValue("approved");

                // Create a new entry in the Payments node with a pending status
                String paymentId = paymentsRef.push().getKey();
                if (paymentId != null) {
                    String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().getTime());

                    HashMap<String, Object> paymentData = new HashMap<>();
                    paymentData.put("amount", request.getAmount());
                    paymentData.put("category", request.getCategory());
                    paymentData.put("dateTime", currentDateTime);
                    paymentData.put("inventoryManager", request.getInventoryManager());
                    paymentData.put("itemName", request.getItemName());
                    paymentData.put("requestCount", request.getRequestCount());
                    paymentData.put("status", "pending");

                    paymentsRef.child(paymentId).setValue(paymentData);

                    // Create entries in PaidRequests and FinanceReports
                    createPaidRequest(request, paymentId, currentDateTime);
                    createFinanceReport(request, paymentId, currentDateTime);
                }
            }
        }
    }

    private void createPaidRequest(FinanceRequestModel request, String paymentId, String dateTime) {
        String paidRequestId = paidRequestsRef.push().getKey();
        if (paidRequestId != null) {
            HashMap<String, Object> paidRequestData = new HashMap<>();
            paidRequestData.put("amount", request.getAmount());
            paidRequestData.put("category", request.getCategory());
            paidRequestData.put("dateTime", dateTime);
            paidRequestData.put("inventoryManager", request.getInventoryManager());
            paidRequestData.put("itemName", request.getItemName());
            paidRequestData.put("requestCount", request.getRequestCount());
            paidRequestData.put("status", "approved"); // Status set to approved

            paidRequestsRef.child(paidRequestId).setValue(paidRequestData);
        }
    }

    private void createFinanceReport(FinanceRequestModel request, String paymentId, String dateTime) {
        String reportId = financeReportsRef.push().getKey();
        if (reportId != null) {
            HashMap<String, Object> reportData = new HashMap<>();
            reportData.put("amount", request.getAmount());
            reportData.put("category", request.getCategory());
            reportData.put("dateTime", dateTime);
            reportData.put("inventoryManager", request.getInventoryManager());
            reportData.put("itemName", request.getItemName());
            reportData.put("requestCount", request.getRequestCount());
            reportData.put("status", "approved"); // Status set to approved

            financeReportsRef.child(reportId).setValue(reportData);
        }
    }

    private void rejectRequest(FinanceRequestModel request) {
        if (request.getRequestId() != null) {
            // Update the request status to "rejected"
            financeRequestsRef.child(request.getRequestId()).child("status").setValue("rejected");

            // Create entry in FinanceReports with status set to rejected
            createFinanceReportForRejection(request);
        }
    }

    private void createFinanceReportForRejection(FinanceRequestModel request) {
        String reportId = financeReportsRef.push().getKey();
        if (reportId != null) {
            HashMap<String, Object> reportData = new HashMap<>();
            reportData.put("amount", request.getAmount());
            reportData.put("category", request.getCategory());
            reportData.put("dateTime", request.getDateTime()); // Use original dateTime from request
            reportData.put("inventoryManager", request.getInventoryManager());
            reportData.put("itemName", request.getItemName());
            reportData.put("requestCount", request.getRequestCount());
            reportData.put("status", "rejected"); // Status set to rejected

            financeReportsRef.child(reportId).setValue(reportData);
        }
    }

    public static class FinanceRequestViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemCategoryFN, dateTime, inventoryManagerName, rqstcount, amountTextView, statusTextView;
        Button approveButton, rejectButton;

        public FinanceRequestViewHolder(@NonNull View itemView) {
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
