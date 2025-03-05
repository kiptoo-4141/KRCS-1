package com.kenyaredcross.viewholder;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.kenyaredcross.R;

public class CourseViewHolder extends RecyclerView.ViewHolder {
    public TextView courseTitle, courseDesc;

    public CourseViewHolder(View itemView) {
        super(itemView);
        courseTitle = itemView.findViewById(R.id.courseTitle);
        courseDesc = itemView.findViewById(R.id.courseDesc);
    }
}
