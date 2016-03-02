package com.khasang.vkphoto.domain.adapters.viewholders;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalDataSource;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.AlbumsPresenter;
import com.khasang.vkphoto.util.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class PhotoAlbumViewHolder extends MultiSelectorBindingHolder implements View.OnLongClickListener, View.OnClickListener {
    final private ImageView albumThumbImageView;
    final private TextView albumTitleTextView;
    final private TextView albumPhotoCountTextView;
    final private CheckBox albumSelectedCheckBox;
    final private ExecutorService executor;
    final private MultiSelector multiSelector;
    PhotoAlbum photoAlbum;
    private AlbumsPresenter albumsPresenter;
    private boolean selectable;
    private Handler handler;
    private LocalDataSource localDataSource;

    public PhotoAlbumViewHolder(View itemView, ExecutorService executor, MultiSelector multiSelector, AlbumsPresenter albumsPresenter) {
        super(itemView, multiSelector);
        albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
        albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
        albumPhotoCountTextView = (TextView) itemView.findViewById(R.id.tv_count_of_albums);
        albumSelectedCheckBox = (CheckBox) itemView.findViewById(R.id.cb_selected);

        this.executor = executor;
        this.multiSelector = multiSelector;
        this.albumsPresenter = albumsPresenter;
        handler = new Handler(Looper.getMainLooper());
        localDataSource = new LocalDataSource(albumThumbImageView.getContext().getApplicationContext());

        itemView.setLongClickable(true);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        albumSelectedCheckBox.setOnClickListener(this);
    }

    public void bindPhotoAlbum(final PhotoAlbum photoAlbum) {
        this.photoAlbum = photoAlbum;
        albumTitleTextView.setText(photoAlbum.title);
        albumPhotoCountTextView.setText(albumPhotoCountTextView.getContext().getString(R.string.count_of_photos_in_album, photoAlbum.size));
        loadThumb();
    }

    private void loadThumb() {
        if (!TextUtils.isEmpty(photoAlbum.thumbFilePath)) {
            File file = new File(photoAlbum.thumbFilePath);
            if (file.exists()) {
                loadPhoto(file);
            }
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!setThumb(getAlbumThumb())) {
                        setThumb(getAlbumThumb());
                    }
                }

                private File getAlbumThumb() {
                    return albumsPresenter.getAlbumThumb(localDataSource.getPhotoSource(), photoAlbum, executor);
                }
            });
        }
    }

    private boolean setThumb(File thumb) {
        boolean success = false;
        if (thumb != null && thumb.exists() && thumb.getAbsolutePath().equals(photoAlbum.thumbFilePath)) {
            loadPhoto(thumb);
            success = true;
        } else if (photoAlbum.thumb_id != Constants.NULL) {
            thumb = localDataSource.getPhotoSource().getLocalPhotoFile(photoAlbum.thumb_id);
            if (thumb != null) {
                photoAlbum.thumbFilePath = thumb.getAbsolutePath();
                localDataSource.getAlbumSource().updateAlbum(photoAlbum);
                loadPhoto(thumb);
                success = true;
            }
        } else if (TextUtils.isEmpty(photoAlbum.thumbFilePath)) {
            loadPhoto(R.drawable.vk_gray_transparent_shape);
            success = true;
        }
        return success;
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
            albumsPresenter.selectAlbum(multiSelector, (AppCompatActivity) albumThumbImageView.getContext());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (multiSelector.isSelectable()) {
            multiSelector.tapSelection(this);
            albumsPresenter.checkActionModeFinish(multiSelector);
        } else {
            albumsPresenter.goToPhotoAlbum(v.getContext(), photoAlbum);
        }
    }

    @Override
    public boolean isSelectable() {
        return selectable;
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
    public boolean isActivated() {
        return albumSelectedCheckBox.isChecked();
    }

    @Override
    public void setActivated(boolean b) {
        albumSelectedCheckBox.setChecked(b);
    }
}