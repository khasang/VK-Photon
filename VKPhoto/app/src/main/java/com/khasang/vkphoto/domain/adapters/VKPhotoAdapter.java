package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.model.Photo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VKPhotoAdapter extends BaseAdapter {
    private Context context;
    private List<Photo> photoList;

    public VKPhotoAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
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
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_simple_photo, null);
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.image_view),
                    (ProgressBar) convertView.findViewById(R.id.progressBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(context).load(photoList.get(position).getUrlToMaxPhoto()).error(R.drawable.vk_share_send_button_background).into(viewHolder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;

        public ViewHolder(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }
    }
}
