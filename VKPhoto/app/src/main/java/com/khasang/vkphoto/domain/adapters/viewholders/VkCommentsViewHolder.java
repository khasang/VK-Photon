package com.khasang.vkphoto.domain.adapters.viewholders;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khasang.vkphoto.R;

public class VkCommentsViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView text;
    public ImageView userImage;
    public TextView date;
    public TextView commentsLikes;
    public LinearLayout isCommentLikes;

    public VkCommentsViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.recyclerViewName);
        text = (TextView) itemView.findViewById(R.id.recyclerViewUserText);
        date = (TextView) itemView.findViewById(R.id.recyclerViewDate);
        commentsLikes = (TextView) itemView.findViewById(R.id.commentsLikes);
        userImage = (ImageView) itemView.findViewById(R.id.recyclerViewUserImage);
        isCommentLikes = (LinearLayout) itemView.findViewById(R.id.isCommentLikes);
        Context context = name.getContext();
        Typeface fromAsset = Typeface.createFromAsset(
                context.getAssets(), "fonts/plain.ttf");
        name.setTypeface(fromAsset);
        text.setTypeface(fromAsset);
        date.setTypeface(fromAsset);
        commentsLikes.setTypeface(fromAsset);
    }

}
