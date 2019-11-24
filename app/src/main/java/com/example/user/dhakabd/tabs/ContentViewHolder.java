package com.example.user.dhakabd.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.dhakabd.R;

public class ContentViewHolder extends RecyclerView.ViewHolder {
    public ImageView mImageView;
    public TextView mTitle;
    public LinearLayout mContentItemLinearLayout;

    public ContentViewHolder(View itemView) {
        super(itemView);

        mImageView = itemView.findViewById(R.id.content_image);
        mTitle = itemView.findViewById(R.id.content_image_title);
        mContentItemLinearLayout = itemView.findViewById(R.id.content_item_linearLayout);
    }
}
