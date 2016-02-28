package com.khasang.vkphoto.domain.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenter;
import com.khasang.vkphoto.util.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class PhotoAlbumViewHolder extends MultiSelectorBindingHolder implements View.OnLongClickListener, View.OnClickListener {
    final private ImageView albumThumbImageView;
    final private TextView albumTitleTextView;
    final private TextView albumPhotoCountTextView;
    final private CheckBox albumSelectedCheckBox;
    private VKAlbumsPresenter vkAlbumsPresenter;
    final private ExecutorService executor;
    final private MultiSelector multiSelector;
    private boolean selectable;
    PhotoAlbum photoAlbum;
    private Handler handler;

    public PhotoAlbumViewHolder(View itemView, ExecutorService executor, MultiSelector multiSelector, VKAlbumsPresenter vkAlbumsPresenter) {
        super(itemView, multiSelector);
        albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
        albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
        albumPhotoCountTextView = (TextView) itemView.findViewById(R.id.tv_count_of_albums);
        albumSelectedCheckBox = (CheckBox) itemView.findViewById(R.id.cb_selected);
        this.executor = executor;
        this.multiSelector = multiSelector;
        this.vkAlbumsPresenter = vkAlbumsPresenter;
        handler = new Handler(Looper.getMainLooper());
        itemView.setLongClickable(true);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void bindPhotoAlbum(final PhotoAlbum photoAlbum) {
        this.photoAlbum = photoAlbum;
        albumTitleTextView.setText(photoAlbum.title);
        albumPhotoCountTextView.setText(albumPhotoCountTextView.getContext().getString(R.string.count_of_photos_in_album, photoAlbum.size));
        loadThumb();
    }

    private void loadThumb() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (!setPhoto()) {
                    if (photoAlbum.thumb_id != Constants.NULL) {
                        vkAlbumsPresenter.downloadAlbumThumb(new LocalPhotoSource(albumThumbImageView.getContext()), photoAlbum, executor);
                        setPhoto();
                    } else {
                        loadPhoto(R.drawable.vk_gray_transparent_shape);
                    }
                }
            }
        });
    }

    private boolean setPhoto() {
        final File photoById = new LocalPhotoSource(albumThumbImageView.getContext().getApplicationContext()).getLocalPhotoFile(photoAlbum.thumb_id);
        if (photoById != null) {
            loadPhoto(photoById);
            return true;
        }
        return false;
    }

    private void loadPhoto(final File file) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(albumThumbImageView.getContext()).load(file).fit().centerCrop().error(R.drawable.vk_share_send_button_background).into(albumThumbImageView);
            }
        });
    }

    private void loadPhoto(final int resource) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(albumThumbImageView.getContext()).load(resource).fit().centerCrop().error(R.drawable.vk_share_send_button_background).into(albumThumbImageView);
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        if (!multiSelector.isSelectable()) { // (3)
            multiSelector.setSelectable(true); // (4)
            multiSelector.setSelected(this, true); // (5)
            vkAlbumsPresenter.selectAlbum(multiSelector, (AppCompatActivity) albumThumbImageView.getContext());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (multiSelector.isSelectable()) {
            multiSelector.tapSelection(this);
            vkAlbumsPresenter.checkActionModeFinish(multiSelector);
        } else {
            vkAlbumsPresenter.goToPhotoAlbum(v.getContext(), photoAlbum);
        }
    }

    @Override
    public void setSelectable(boolean b) {
        selectable = b;
        if (selectable) {
            albumSelectedCheckBox.setVisibility(View.VISIBLE);
        } else {
            albumSelectedCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setActivated(boolean b) {
        albumSelectedCheckBox.setChecked(b);
    }

    @Override
    public boolean isActivated() {
        return albumSelectedCheckBox.isChecked();
    }
}