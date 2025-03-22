package com.kenyaredcross.activity;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kenyaredcross.R;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private final List<Uri> documentUris;

    public DocumentAdapter(List<Uri> documentUris) {
        this.documentUris = documentUris;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Uri documentUri = documentUris.get(position);

        // Get the file name from the URI
        String fileName = getFileNameFromUri(documentUri);
        holder.tvDocumentName.setText(fileName);

        // Set document type icon based on file extension
        setDocumentIcon(holder.ivDocumentIcon, fileName);

        // Setup remove button
        holder.ivRemoveDocument.setOnClickListener(v -> {
            documentUris.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return documentUris.size();
    }

    private String getFileNameFromUri(Uri uri) {
        String path = uri.getLastPathSegment();
        if (path != null) {
            // Extract file name from path
            int lastSlashIndex = path.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < path.length() - 1) {
                return path.substring(lastSlashIndex + 1);
            }
            return path;
        }
        return "Unknown Document";
    }

    private void setDocumentIcon(ImageView imageView, String fileName) {
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }

        // Set icon based on file extension
        switch (extension) {
            case "pdf":
                imageView.setImageResource(R.drawable.ic_pdf_document);
                break;
            case "doc":
            case "docx":
                imageView.setImageResource(R.drawable.ic_word_document);
                break;
            case "jpg":
            case "jpeg":
            case "png":
                imageView.setImageResource(R.drawable.ic_image_document);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_file_document);
                break;
        }
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocumentName;
        ImageView ivDocumentIcon;
        ImageView ivRemoveDocument;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDocumentName = itemView.findViewById(R.id.tvDocumentName);
            ivDocumentIcon = itemView.findViewById(R.id.ivDocumentIcon);
            ivRemoveDocument = itemView.findViewById(R.id.ivRemoveDocument);
        }
    }
}