package com.khasang.vkphoto.domain.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.vkphoto.R;

/**
 * Created by admin on 09.03.2016.
 */
public class VkCommentsViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView text;
        public ImageView userImage;
        public TextView date;

        public VkCommentsViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.recyclerViewName);
            text = (TextView) itemView.findViewById(R.id.recyclerViewUserText);
            date = (TextView) itemView.findViewById(R.id.recyclerViewDate);
            userImage = (ImageView) itemView.findViewById(R.id.recyclerViewUserImage);
        }
}
