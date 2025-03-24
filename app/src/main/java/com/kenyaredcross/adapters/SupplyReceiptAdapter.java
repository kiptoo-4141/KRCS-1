package com.kenyaredcross.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.PaidRequest;
import com.kenyaredcross.domain_model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SupplyReceiptAdapter extends RecyclerView.Adapter<SupplyReceiptAdapter.ViewHolder> {
    private final List<PaidRequest> paidRequests;
    private final List<User> users;
    private final Context context;

    public SupplyReceiptAdapter(List<PaidRequest> paidRequests, List<User> users, Context context) {
        this.paidRequests = paidRequests;
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supply_receipt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= paidRequests.size() || position >= users.size()) {
            return; // Prevent index out of bounds
        }

        PaidRequest paidRequest = paidRequests.get(position);
        User user = users.get(position);

        holder.companyName.setText(user.getUsername());
        holder.email.setText("Email: " + user.getEmail());

        holder.itemName.setText("Item: " + paidRequest.getItemName());
        holder.amount.setText("Amount: KES " + paidRequest.getAmount());

        // Format the date if possible
        String dateTime = paidRequest.getDateTime();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
            Date date = inputFormat.parse(dateTime);
            if (date != null) {
                dateTime = outputFormat.format(date);
            }
        } catch (Exception e) {
            // Use the original date string if parsing fails
        }

        holder.dateTime.setText("Date: " + dateTime);

        // Download PDF on item click
        holder.itemView.setOnClickListener(v -> {
            String receiptDetails = generateReceiptDetails(user, paidRequest);
            saveReceiptAsPdf(receiptDetails, user.getUsername(), paidRequest.getItemName());
        });
    }

    private String generateReceiptDetails(User user, PaidRequest paidRequest) {
        return "KENYA RED CROSS SOCIETY\n"
                + "SUPPLY RECEIPT\n\n"
                + "Supplier: " + user.getUsername() + "\n"
                + "Email: " + user.getEmail() + "\n"
                + "Item: " + paidRequest.getItemName() + "\n"
                + "Category: " + paidRequest.getCategory() + "\n"
                + "Amount: KES " + paidRequest.getAmount() + "\n"
                + "Date: " + paidRequest.getDateTime() + "\n"
                + "Status: " + paidRequest.getStatus().toUpperCase() + "\n\n"
                + "Inventory Manager: " + paidRequest.getInventoryManager() + "\n\n"
                + "This receipt serves as proof of payment for goods/services provided.\n"
                + "Thank you for your service.\n\n"
                + "Receipt generated on: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date());
    }

    @Override
    public int getItemCount() {
        // Return the minimum size to prevent IndexOutOfBoundsException
        return Math.min(paidRequests.size(), users.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyName, email, itemName, amount, dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName);
            email = itemView.findViewById(R.id.email);

            itemName = itemView.findViewById(R.id.itemName);
            amount = itemView.findViewById(R.id.amount);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }

    private void saveReceiptAsPdf(String receiptDetails, String companyName, String itemName) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Set up colors
        paint.setColor(Color.BLACK);
        int redColor = Color.rgb(214, 37, 37); // Red color for headers

        // Draw header
        paint.setColor(redColor);
        paint.setTextSize(22);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("KENYA RED CROSS SOCIETY", 50, 50, paint);

        paint.setTextSize(18);
        canvas.drawText("SUPPLY RECEIPT", 50, 80, paint);

        // Draw line under header
        paint.setStrokeWidth(2);
        canvas.drawLine(50, 90, 545, 90, paint);

        // Draw receipt content
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        String[] lines = receiptDetails.split("\n");
        int y = 130;
        for (String line : lines) {
            // Bold headings
            if (line.contains("KENYA RED CROSS") || line.contains("SUPPLY RECEIPT")) {
                continue; // Skip these as they're already drawn
            } else if (line.isEmpty()) {
                y += 10; // Add smaller spacing for empty lines
            } else if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText(parts[0] + ":", 50, y, paint);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                canvas.drawText(parts.length > 1 ? parts[1] : "", 50 + paint.measureText(parts[0] + ": "), y, paint);
                y += 25;
            } else {
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                canvas.drawText(line, 50, y, paint);
                y += 25;
            }
        }

        // Draw footer
        paint.setColor(redColor);
        paint.setTextSize(12);
        canvas.drawText("Kenya Red Cross Society © " + new SimpleDateFormat("yyyy", Locale.US).format(new Date()), 50, 780, paint);

        document.finishPage(page);

        // Create a valid filename from company and item name
        String sanitizedCompany = companyName.replaceAll("[^a-zA-Z0-9]", "_");
        String sanitizedItem = itemName.replaceAll("[^a-zA-Z0-9]", "_");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "KRCS_Receipt_" + sanitizedCompany + "_" + sanitizedItem + "_" + timestamp + ".pdf";

        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File file = new File(downloadsDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            fos.close();
            document.close();

            Toast.makeText(context, "Receipt saved to Downloads as " + fileName, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save receipt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}