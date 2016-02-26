package com.khasang.vkphoto.domain.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.MyVkRequestListener;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenter;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.JsonUtils;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKResponse;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PhotoAlbumViewHolder extends SwappingHolder implements View.OnLongClickListener, View.OnClickListener {
    final private ImageView albumThumbImageView;
    final private TextView albumTitleTextView;
    final private TextView albumPhotoCountTextView;
    final private ExecutorService executor;
    final private MultiSelector multiSelector;
    private VKAlbumsPresenter vkAlbumsPresenter;
    PhotoAlbum photoAlbum;
    private ActionMode actionMode;
    private Handler handler;

    public PhotoAlbumViewHolder(View itemView, ExecutorService executor, MultiSelector multiSelector, VKAlbumsPresenter vkAlbumsPresenter) {
        super(itemView, multiSelector);
        albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
        albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
        albumPhotoCountTextView = (TextView) itemView.findViewById(R.id.tv_count_of_albums);
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
        String photoCount = albumPhotoCountTextView.getContext().getString(R.string.count_of_photos_in_album, photoAlbum.size);
        albumPhotoCountTextView.setText(photoCount);
        loadThumb(photoAlbum);
    }

    private void loadThumb(final PhotoAlbum photoAlbum) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (!setPhoto(photoAlbum)) {
                    if (photoAlbum.thumb_id != Constants.NULL) {
                        RequestMaker.getPhotoAlbumThumb(new MyVkRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                try {
                                    final Photo photo = JsonUtils.getItems(response.json, Photo.class).get(0);
                                    Callable<File> saveFile = new Callable<File>() {
                                        @Override
                                        public File call() throws Exception {
                                            return new LocalPhotoSource(albumThumbImageView.getContext().getApplicationContext()).savePhotoToAlbum(photo, photoAlbum);
                                        }
                                    };
                                    Future<File> fileFuture = executor.submit(saveFile);
                                    File file = fileFuture.get();
                                    if (file != null) {
                                        loadPhoto(file);
                                    }
                                } catch (Exception e) {
                                    sendError(e.toString());
                                }
                            }
                        }, photoAlbum);
                    } else {
                        Picasso.with(albumThumbImageView.getContext()).load(R.drawable.vk_gray_transparent_shape).into(albumThumbImageView);
                    }
                }
            }
        });
    }

    private boolean setPhoto(PhotoAlbum photoAlbum) {
        final File photoById = new LocalPhotoSource(albumThumbImageView.getContext().getApplicationContext()).getLocalPhotoFile(photoAlbum.thumb_id);
        if (photoById != null) {
            loadPhoto(photoById);
            return true;
        }
        return false;
    }

    private void loadPhoto(final File photoById) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(albumThumbImageView.getContext()).load(photoById).into(albumThumbImageView);
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        if (!multiSelector.isSelectable()) { // (3)
            final AppCompatActivity activity = (AppCompatActivity) albumThumbImageView.getContext();
            multiSelector.setSelectable(true); // (4)
            multiSelector.setSelected(this, true); // (5)
            actionMode = activity.startSupportActionMode(new ModalMultiSelectorCallback(multiSelector) {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    activity.getMenuInflater().inflate(R.menu.menu_action_mode_vk_albums, menu);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    super.onDestroyActionMode(actionMode);
                    multiSelector.clearSelections();
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_sync_album:
                            vkAlbumsPresenter.syncAlbums(multiSelector);
                            return true;
                        case R.id.action_delete_album:
//                            vkAlbumsPresenter.deleteVkAlbums(multiSelector);
                            return true;
                        default:
                            break;
                    }
                    return false;
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (multiSelector.isSelectable()) {
            multiSelector.tapSelection(this);
            if (multiSelector.getSelectedPositions().size() == 0) {
                multiSelector.setSelectable(false);
                if (actionMode != null) {
                    actionMode.finish();
                }
            }
        } else {
            vkAlbumsPresenter.goToPhotoAlbum(v.getContext(), photoAlbum);
        }
    }

}