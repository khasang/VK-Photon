package com.khasang.vkphoto.domain.adapters.viewholders;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.MultiSelectorBindingHolder;
import com.bumptech.glide.Glide;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalDataSource;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.tasks.DownloadPhotoCallable;
import com.khasang.vkphoto.presentation.activities.MainActivity;
import com.khasang.vkphoto.presentation.model.MyVkRequestListener;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.AlbumsPresenter;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKResponse;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PhotoAlbumViewHolder extends MultiSelectorBindingHolder implements View.OnLongClickListener, View.OnClickListener {
    final private ImageView albumThumbImageView;
    final private TextView albumTitleTextView;
    final private TextView albumPhotoCountTextView;
    final private CheckBox albumSelectedCheckBox;
    final private ExecutorService executor;
    final private MultiSelector multiSelector;
    final private Map<Integer, Future<File>> downloadFutures;
    final private ProgressBar progressBar;
    final private ImageView ivSyncStatus;
    PhotoAlbum photoAlbum;
    MenuItem menuItem;
    private AlbumsPresenter albumsPresenter;
    private boolean selectable;
    private Handler handler;
    private LocalDataSource localDataSource;

    public PhotoAlbumViewHolder(View itemView, ExecutorService executor, MultiSelector multiSelector, AlbumsPresenter albumsPresenter, Map<Integer, Future<File>> downloadFutures) {
        super(itemView, multiSelector);
        albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
        albumThumbImageView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                MainActivity.ALBUM_THUMB_HEIGHT
        ));
        albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
        albumPhotoCountTextView = (TextView) itemView.findViewById(R.id.tv_count_of_photos);
        albumSelectedCheckBox = (CheckBox) itemView.findViewById(R.id.cb_selected);
        ivSyncStatus = (ImageView) itemView.findViewById(R.id.iv_sync_status);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        this.downloadFutures = downloadFutures;
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
        albumThumbImageView.setImageDrawable(null);
        albumTitleTextView.setText(photoAlbum.title);
        albumTitleTextView.setTypeface(Typeface.createFromAsset(
                albumTitleTextView.getContext().getAssets(), "fonts/plain.ttf"));
        albumPhotoCountTextView.setText(String.valueOf(photoAlbum.size));
        albumPhotoCountTextView.setTypeface(Typeface.createFromAsset(
                albumPhotoCountTextView.getContext().getAssets(), "fonts/plain.ttf"));
        Logger.d("bindPhotoAlbum. ID=" + photoAlbum.id + ", name=" + photoAlbum.title + ", size=" + photoAlbum.size);
        changeSyncVisibility(photoAlbum);
        loadThumb();
    }

    private void changeSyncVisibility(PhotoAlbum photoAlbum) {
        switch (photoAlbum.syncStatus) {
            case Constants.SYNC_NOT_STARTED:
                progressBar.setVisibility(View.INVISIBLE);
                ivSyncStatus.setVisibility(View.INVISIBLE);
                break;
            case Constants.SYNC_STARTED:
                progressBar.setVisibility(View.VISIBLE);
                ivSyncStatus.setVisibility(View.INVISIBLE);
                break;
            case Constants.SYNC_SUCCESS:
                progressBar.setVisibility(View.INVISIBLE);
                ivSyncStatus.setVisibility(View.VISIBLE);
                ivSyncStatus.setImageResource(R.drawable.ic_action_tick);
                break;
            case Constants.SYNC_FAILED:
                progressBar.setVisibility(View.INVISIBLE);
                ivSyncStatus.setVisibility(View.VISIBLE);
                ivSyncStatus.setImageResource(R.drawable.ic_sync_problem);
                break;
        }
        if (photoAlbum.syncStatus == Constants.SYNC_NOT_STARTED) {
            ivSyncStatus.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void loadThumb() {
        if (!TextUtils.isEmpty(photoAlbum.thumbFilePath)) {
            File file = new File(photoAlbum.thumbFilePath);
            if (file.exists()) {
                Logger.d("photoAlbum thumb exists " + photoAlbum.thumbFilePath);
                loadPhoto(file);
            }
        } else if (photoAlbum.thumb_id > 0) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    Logger.d("photoAlbum " + photoAlbum.title + " try do download thumb");
                    if (!setThumb(getAlbumThumb())) {
                        setThumb(getAlbumThumb());
                    }
                }

                private File getAlbumThumb() {
                    final File[] files = new File[1];
                    RequestMaker.getPhotoByIdSync(new MyVkRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            try {
                                final Photo photo = JsonUtils.getPhotos(response.json, Photo.class).get(0);
                                Future<File> fileFuture = executor.submit(new DownloadPhotoCallable(new LocalPhotoSource(albumThumbImageView.getContext()),
                                        photo, photoAlbum));
                                addFuture(photo.id, fileFuture);
                                files[0] = fileFuture.get();
                                removeFuture(photo);
                            } catch (Exception e) {
                                sendError(ErrorUtils.JSON_PARSE_FAILED);
                            }
                        }
                    }, photoAlbum.thumb_id);
                    return files[0];
                }
            });
        } else if (photoAlbum.size == 0) {
            loadPhoto(photoAlbum.owner_id == 0 ? R.mipmap.no_thumb_local : R.mipmap.no_thumb_vk);
        }
    }

    private void removeFuture(Photo photo) {
        synchronized (downloadFutures) {
            downloadFutures.remove(photo.id);
        }
    }

    private void addFuture(int photoId, Future<File> fileFuture) {
        synchronized (downloadFutures) {
            if (downloadFutures.containsKey(photoId)) {
                downloadFutures.get(photoId).cancel(true);
                downloadFutures.remove(photoId);
            }
            downloadFutures.put(photoId, fileFuture);
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
                localDataSource.getAlbumSource().updateAlbum(photoAlbum, true);
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
                try {
                    Glide.with(albumThumbImageView.getContext())
                            .load(file)
                            .centerCrop()
                            .crossFade()
                            .error(R.drawable.vk_share_send_button_background)
                            .into(albumThumbImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadPhoto(final int resource) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(albumThumbImageView.getContext())
                        .load(resource)
                        .centerCrop()
                        .crossFade()
                        .error(R.drawable.vk_share_send_button_background)
                        .into(albumThumbImageView);
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
            albumsPresenter.hideActionModeItem(multiSelector, menuItem);
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