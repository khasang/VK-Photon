package com.khasang.vkphoto.domain.adapters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder;
import com.bumptech.glide.Glide;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.presenter.album.AlbumPresenter;
import com.khasang.vkphoto.util.Logger;

import java.util.List;

public class PhotoAlbumAdapter extends RecyclerView.Adapter<PhotoAlbumAdapter.ViewHolder> {
    private MultiSelector multiSelector;
    private List<Photo> photoList;
    private AlbumPresenter albumPresenter;

    public PhotoAlbumAdapter(MultiSelector multiSelector, List<Photo> photoList, AlbumPresenter albumPresenter) {
        this.multiSelector = multiSelector;
        this.photoList = photoList;
        this.albumPresenter = albumPresenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_photo, parent, false);
        return new ViewHolder(view, multiSelector, albumPresenter);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindPhotoAlbum(photoList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList.clear();
        this.photoList.addAll(photoList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends MultiSelectorBindingHolder implements View.OnClickListener, View.OnLongClickListener {
        final private ImageView imageView;
        final private CheckBox checkBox;
        private MultiSelector multiSelector;
        private boolean selectable;
        private AlbumPresenter localAlbumPresenter;
        private Photo photo;
        private int position;

        public ViewHolder(View itemView, MultiSelector multiSelector, AlbumPresenter localAlbumPresenter) {
            super(itemView, multiSelector);
            this.multiSelector = multiSelector;
            this.localAlbumPresenter = localAlbumPresenter;
            this.imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.cb_photo_selected);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            checkBox.setOnClickListener(this);
        }

        @Override
        public boolean isSelectable() {
            return selectable;
        }

        @Override
        public void setSelectable(boolean b) {
            selectable = b;
            if (selectable) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean isActivated() {
            return checkBox.isChecked();
        }

        @Override
        public void setActivated(boolean b) {
            checkBox.setChecked(b);
        }

        public void bindPhotoAlbum(Photo photo, int position) {
            this.photo = photo;
            this.position = position;
            if (!TextUtils.isEmpty(photo.filePath)) {
                Glide.with(imageView.getContext())
                        .load("file://" + photo.filePath)
                        .centerCrop()
                        .crossFade()
                        .error(R.drawable.vk_share_send_button_background)
                        .into(imageView);
            } else {
                Glide.with(imageView.getContext())
                        .load(photo.photo_130)
                        .centerCrop()
                        .crossFade()
                        .error(R.drawable.vk_share_send_button_background)
                        .into(imageView);
            }
        }

        @Override
        public void onClick(View v) {
            if (multiSelector.isSelectable()) {
                multiSelector.tapSelection(this);
                localAlbumPresenter.checkActionModeFinish(multiSelector);
            } else {
//                        albumPresenter.goToPhotoAlbum(v.getContext(), photoAlbum);
                    //Navigator.navigateToVKCommentsFragment(v.getContext(), photo);
                Navigator.navigateToPhotoViewPagerFragment(v.getContext(),photoList, this.position);
            }
            if (photo.filePath != null) {
                Logger.d(photo.filePath);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (!multiSelector.isSelectable()) { // (3)
                multiSelector.setSelectable(true); // (4)
                multiSelector.setSelected(this, true); // (5)
                localAlbumPresenter.selectPhoto(multiSelector, (AppCompatActivity) imageView.getContext());
                return true;
            }
            return false;
        }
    }
}
