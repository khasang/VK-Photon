package com.khasang.vkphoto.domain.adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.presenter.album.LocalAlbumPresenter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAlbumAdapter extends RecyclerView.Adapter<PhotoAlbumAdapter.ViewHolder> {
    private MultiSelector multiSelector;
    private List<Photo> photoList;
    private LocalAlbumPresenter localAlbumPresenter;

    public PhotoAlbumAdapter(MultiSelector multiSelector, List<Photo> photoList, LocalAlbumPresenter localAlbumPresenter) {
        this.multiSelector = multiSelector;
        this.photoList = photoList;
        this.localAlbumPresenter = localAlbumPresenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_simple_photo, parent, false);
        return new ViewHolder(view, multiSelector, localAlbumPresenter);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindPhotoAlbum(photoList.get(position));
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

    public static class ViewHolder extends MultiSelectorBindingHolder implements View.OnClickListener, View.OnLongClickListener {
        final private ImageView imageView;
        final private CheckBox checkBox;
        private MultiSelector multiSelector;
        private boolean selectable;
        private LocalAlbumPresenter localAlbumPresenter;
        private Photo photo;

        public ViewHolder(View itemView, MultiSelector multiSelector, LocalAlbumPresenter localAlbumPresenter) {
            super(itemView, multiSelector);
            this.multiSelector = multiSelector;
            this.localAlbumPresenter = localAlbumPresenter;
            this.imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.cb_photo_selected);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
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

        public void bindPhotoAlbum(Photo photo) {
            this.photo = photo;
            Picasso.with(imageView.getContext()).load("file://" + photo.filePath).fit()
                    .centerCrop().error(R.drawable.vk_share_send_button_background).into(imageView);

        }

        @Override
        public void onClick(View v) {
            if (multiSelector.isSelectable()) {
                multiSelector.tapSelection(this);
                localAlbumPresenter.checkActionModeFinish(multiSelector);
            } else {
//                        localAlbumPresenter.goToPhotoAlbum(v.getContext(), photoAlbum);
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
