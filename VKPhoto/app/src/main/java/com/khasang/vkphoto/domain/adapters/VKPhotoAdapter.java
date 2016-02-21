package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Anton on 21.02.2016.
 */
public class VKPhotoAdapter extends BaseAdapter {
    private Context context;
    private List<Photo> photoList;

    public VKPhotoAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_simple_photo, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
        Picasso.with(context).load(photoList.get(position).photo_604).into(imageView);
        return convertView;
    }
}
