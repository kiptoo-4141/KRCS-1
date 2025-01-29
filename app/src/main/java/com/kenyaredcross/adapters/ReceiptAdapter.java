// Receipt Adapter
package com.kenyaredcross.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;
import com.kenyaredcross.domain_model.Receipt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {
    private final Context context;
    private final List<Receipt> receiptList;

    public ReceiptAdapter(Context context, List<Receipt> receiptList) {
        this.context = context;
        this.receiptList = receiptList;
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receipt, parent, false);
        return new ReceiptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        Receipt receipt = receiptList.get(position);

        holder.tvCourseId.setText("Course ID: " + receipt.getCourseId());
        holder.tvDate.setText(receipt.getDate());
        holder.tvPaymentMethod.setText("Payment Method: " + receipt.getPaymentMethod());

        holder.btnDownload.setOnClickListener(v -> downloadReceipt(receipt));
    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }

    private void downloadReceipt(Receipt receipt) {
        // Create a PDF document
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        // Page size and dimensions
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Load the company logo
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.th);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 100, 100, false);
        canvas.drawBitmap(scaledLogo, 50, 30, paint);

        // Add title
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(24);
        canvas.drawText("Receipt", pageInfo.getPageWidth() / 2, 150, titlePaint);

        // Add receipt details
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(16);
        int y = 200; // Starting y-coordinate
        int lineSpacing = 30; // Space between lines

        canvas.drawText("Course ID: " + receipt.getCourseId(), 50, y, paint);
        y += lineSpacing;
        canvas.drawText("Date: " + receipt.getDate(), 50, y, paint);
        y += lineSpacing;
        canvas.drawText("Payment Method: " + receipt.getPaymentMethod(), 50, y, paint);
        y += lineSpacing;
        canvas.drawText("Amount: " + receipt.getAmount(), 50, y, paint);
        y += lineSpacing;
        canvas.drawText("Payment Details: " + receipt.getPaymentDetails(), 50, y, paint);

        // Finish the page
        pdfDocument.finishPage(page);

        // Save the PDF file
        File file = new File(context.getExternalFilesDir(null), "receipt_" + receipt.getCourseId() + ".pdf");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            pdfDocument.writeTo(fos);
            Toast.makeText(context, "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }


    public static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseId, tvDate, tvPaymentMethod;
        Button btnDownload;

        public ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCourseId = itemView.findViewById(R.id.tv_course_id);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            btnDownload = itemView.findViewById(R.id.btn_download_receipt);
        }
    }
}