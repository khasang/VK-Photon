package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.domain.DownloadFileAsyncTask;
import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Constants;
import com.khasang.vkphoto.util.JsonUtils;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoAlbumCursorAdapter extends CursorRecyclerViewAdapter<PhotoAlbumCursorAdapter.ViewHolder> {
    private ExecutorService executor;
    private MultiSelector multiSelector;

    public PhotoAlbumCursorAdapter(Context context, Cursor cursor, MultiSelector multiSelector) {
        super(context, cursor);
        executor = Executors.newCachedThreadPool();
        this.multiSelector = multiSelector;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bindPhotoAlbum(new PhotoAlbum(cursor));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoalbum_item, parent, false);
        return new ViewHolder(view, executor, multiSelector);
    }

    public static class ViewHolder extends SwappingHolder implements View.OnLongClickListener, View.OnClickListener {
        final ImageView albumThumbImageView;
        final TextView albumTitleTextView;
        final TextView albumPhotoCountTextView;
        final Executor executor;
        final MultiSelector multiSelector;
        PhotoAlbum photoAlbum;

        public ViewHolder(View itemView, Executor executor, MultiSelector multiSelector) {
            super(itemView, multiSelector);
            albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
            albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
            albumPhotoCountTextView = (TextView) itemView.findViewById(R.id.tv_count_of_albums);
            this.executor = executor;
            this.multiSelector = multiSelector;
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
            if (photoAlbum.thumb_id != Constants.NULL) {
                RequestMaker.getPhotoAlbumThumb(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            Photo photo = JsonUtils.getItems(response.json, Photo.class).get(0);
                            new DownloadFileAsyncTask(albumThumbImageView, photo, photoAlbum).executeOnExecutor(executor, photo.getUrlToMaxPhoto());
                        } catch (Exception e) {
                            sendError(e.toString());
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        sendError(error.toString());
                    }

                    public void sendError(String s) {
                        EventBus.getDefault().postSticky(new ErrorEvent(s));
                    }
                }, photoAlbum);
            } else {
                Picasso.with(albumThumbImageView.getContext()).load(R.drawable.vk_gray_transparent_shape).into(albumThumbImageView);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (!multiSelector.isSelectable()) { // (3)
                multiSelector.setSelectable(true); // (4)
                multiSelector.setSelected(this, true); // (5)
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
                }
            }
        }
    }
}
