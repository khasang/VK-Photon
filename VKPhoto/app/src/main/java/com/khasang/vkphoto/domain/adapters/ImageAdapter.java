package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.vk.sdk.api.model.VKApiPhoto;

import java.util.ArrayList;

/**
 * Created by admin on 13.02.2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> bitmapList;

    public ImageAdapter(Context context, ArrayList<Bitmap> bitmapList) {
        this.context = context;
        this.bitmapList = bitmapList;
    }

    public int getCount() {
        return this.bitmapList.size();
    }

    public Bitmap getItem(int position) {
        return bitmapList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(this.bitmapList.get(position));
        return imageView;
    }
}
