package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 13.02.2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<String> urlList;

    public ImageAdapter(Context context, List<String> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    public int getCount() {
        return this.urlList.size();
    }

    public String getItem(int position) {
        return urlList.get(position);
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

        Picasso.with(context).load(urlList.get(position)).resize(200,400).into(imageView);
        return imageView;
    }
}
