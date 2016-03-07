package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.viewholders.VKPhotoViewHolder;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.presenter.album.VKAlbumPresenter;

import java.util.List;

public class VKPhotoAdapter extends BaseAdapter {
    private boolean loaded;

    private List<Photo> photoList;
    private MultiSelector multiSelector;
    private VKAlbumPresenter vkAlbumPresenter;
    public VKPhotoAdapter(List<Photo> photoList, MultiSelector multiSelector, VKAlbumPresenter vkAlbumPresenter) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_photo, parent, false);
            vkPhotoViewHolder = new VKPhotoViewHolder(convertView, multiSelector, vkAlbumPresenter);
            convertView.setTag(vkPhotoViewHolder);
        } else {
            vkPhotoViewHolder = (VKPhotoViewHolder) convertView.getTag();
        }
        vkPhotoViewHolder.setAdapterPosition(position);
        vkPhotoViewHolder.loadPhoto(photoList.get(position));
        if (position != 0 || !loaded) {
            multiSelector.bindHolder(vkPhotoViewHolder, position, -1);
        }
        loaded = true;
        return convertView;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
