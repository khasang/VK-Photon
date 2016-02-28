package com.khasang.vkphoto.domain.adapters;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Anton on 28.02.2016.
 */
public class VKPhotoViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    ProgressBar progressBar;

    public VKPhotoViewHolder(ImageView imageView, ProgressBar progressBar) {
        super(imageView);
        this.imageView = imageView;
        this.progressBar = progressBar;
    }
}
