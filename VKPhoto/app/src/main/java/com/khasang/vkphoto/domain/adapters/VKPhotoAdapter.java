package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.presenter.VKAlbumPresenter;
import com.khasang.vkphoto.util.Logger;

import java.util.List;

public class VKPhotoAdapter extends BaseAdapter {
    private Context context;
    private List<Photo> photoList;
    private MultiSelector multiSelector;
    private VKAlbumPresenter vkAlbumPresenter;

    public VKPhotoAdapter(Context context, List<Photo> photoList, MultiSelector multiSelector, VKAlbumPresenter vkAlbumPresenter) {
        this.context = context;
        this.photoList = photoList;
        this.multiSelector = multiSelector;
        this.vkAlbumPresenter = vkAlbumPresenter;
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
        final VKPhotoViewHolder vkPhotoViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_simple_photo, null);
            vkPhotoViewHolder = new VKPhotoViewHolder(convertView, multiSelector, vkAlbumPresenter, VKPhotoAdapter.this);
            convertView.setTag(vkPhotoViewHolder);
        } else {
            vkPhotoViewHolder = (VKPhotoViewHolder) convertView.getTag();
        }
        convertView.setTag(R.id.position_key, position);
        vkPhotoViewHolder.loadPhoto(photoList.get(position));
        return convertView;
    }
}
