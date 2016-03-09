package com.khasang.vkphoto.domain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.viewholders.LocalPhotoViewHolder;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.presenter.album.LocalAlbumPresenter;
import java.util.List;

/**
 * Created by TAU on 05.03.2016.
 */
public class LocalPhotoAdapter extends BaseAdapter {
    private boolean loaded;
    private List<Photo> photoList;
    private MultiSelector multiSelector;
    private LocalAlbumPresenter localAlbumPresenter;

    public LocalPhotoAdapter(List<Photo> photoList, MultiSelector multiSelector, LocalAlbumPresenter localAlbumPresenter) {
        this.photoList = photoList;
        this.multiSelector = multiSelector;
        this.localAlbumPresenter = localAlbumPresenter;
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
        final LocalPhotoViewHolder localPhotoViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_photo, parent, false);
            localPhotoViewHolder = new LocalPhotoViewHolder(convertView, multiSelector, localAlbumPresenter);
            convertView.setTag(localPhotoViewHolder);
        } else
            localPhotoViewHolder = (LocalPhotoViewHolder) convertView.getTag();
        localPhotoViewHolder.setAdapterPosition(position);
        localPhotoViewHolder.loadPhoto(photoList.get(position));
        if (position != 0 || !loaded)
            multiSelector.bindHolder(localPhotoViewHolder, position, -1);
        loaded = true;
        return convertView;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
