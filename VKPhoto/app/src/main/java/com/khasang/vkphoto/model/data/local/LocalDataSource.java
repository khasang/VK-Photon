package com.khasang.vkphoto.model.data.local;

import android.content.Context;

public class LocalDataSource {
    private LocalAlbumSource albumSource;
    private LocalPhotoSource photoSource;
    private LocalCommentSource commentSource;
    private Context context;

    public LocalDataSource(Context context) {
        this.context = context;
        this.albumSource = new LocalAlbumSource(context);
    }

    public LocalAlbumSource getAlbumSource() {
        return albumSource;
    }

    public LocalPhotoSource getPhotoSource() {
        return null;
    }

    public LocalCommentSource getCommentSource() {
        return null;
    }
}
