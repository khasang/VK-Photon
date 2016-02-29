package com.khasang.vkphoto.domain.adapters;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SelectableHolder;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.presenter.VKAlbumPresenter;
import com.khasang.vkphoto.util.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class VKPhotoViewHolder implements SelectableHolder, View.OnLongClickListener, View.OnClickListener {
    final private ImageView imageView;
    final private ProgressBar progressBar;
    private VKAlbumPresenter vkAlbumPresenter;
    private MultiSelector multiSelector;
    private boolean selectable;
    final private CheckBox photoSelectedCheckBox;
    private View view;

    public VKPhotoViewHolder(View view, MultiSelector multiSelector, VKAlbumPresenter vkAlbumPresenter) {
        this.view = view;
        imageView = (ImageView) view.findViewById(R.id.iv_photo);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        this.multiSelector = multiSelector;
        this.vkAlbumPresenter = vkAlbumPresenter;
        photoSelectedCheckBox = (CheckBox) view.findViewById(R.id.cb_photo_selected);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        photoSelectedCheckBox.setOnClickListener(this);
    }

    @Override
    public void setSelectable(boolean b) {
        Logger.d("sel");
        selectable = b;
        if (selectable) {
            photoSelectedCheckBox.setVisibility(View.VISIBLE);
        } else {
            photoSelectedCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setActivated(boolean b) {
        photoSelectedCheckBox.setChecked(b);
    }

    @Override
    public boolean isActivated() {
        return photoSelectedCheckBox.isChecked();
    }

    @Override
    public int getAdapterPosition() {
        return (Integer) view.getTag(R.id.position_key);
    }

    @Override
    public long getItemId() {
        return getAdapterPosition();
    }


    @Override
    public void onClick(View v) {
        if (multiSelector.isSelectable()) {
            multiSelector.tapSelection(this);
            vkAlbumPresenter.checkActionModeFinish(multiSelector, v.getContext());
        } else {
            Logger.d("onClick");
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!multiSelector.isSelectable()) {
            multiSelector.setSelectable(true);
            multiSelector.setSelected(this, true);
            vkAlbumPresenter.selectPhoto(multiSelector, (AppCompatActivity) v.getContext());
            return true;
        }
        return false;
    }

    public void loadPhoto(Photo photo) {
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(imageView.getContext()).load(photo.getUrlToMaxPhoto()).error(R.drawable.vk_share_send_button_background).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
