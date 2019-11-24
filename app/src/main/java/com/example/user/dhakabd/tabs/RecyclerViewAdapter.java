package com.example.user.dhakabd.tabs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.dhakabd.DetailsActivity;
import com.example.user.dhakabd.R;
import com.example.user.dhakabd.model.Content;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ContentViewHolder> {

    private List<Content> mContents;
    private Context mContext;
    private String mFragmentName;
    private View mView;

    public RecyclerViewAdapter(List<Content> contents, Context context, String fragmentName) {
        mContents = contents;
        mContext = context;
        mFragmentName = fragmentName;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_content_item, parent, false);

        return new ContentViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        final Content content = mContents.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.rsz_no_image_icon);

        Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(content.getImage()).into(holder.mImageView);
        holder.mTitle.setText(content.getTitle());

        //Clicking the item
        holder.mContentItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent details_intent = new Intent(mContext,DetailsActivity.class);
                details_intent.putExtra("id",content.getContent_id());
                details_intent.putExtra("fragment_name",mFragmentName);

                mContext.startActivity(details_intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContents.size();
    }
}
