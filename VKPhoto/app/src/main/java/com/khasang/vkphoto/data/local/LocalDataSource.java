package com.khasang.vkphoto.data.local;

import android.content.Context;

public class LocalDataSource {
    private LocalAlbumSource albumSource;
    private LocalPhotoSource photoSource;
    private LocalCommentSource commentSource;

    public LocalDataSource(Context context) {
        this.albumSource = new LocalAlbumSource(context);
        this.photoSource = new LocalPhotoSource(context);
        this.commentSource = new LocalCommentSource(context);
    }

    public LocalAlbumSource getAlbumSource() {
        return albumSource;
    }

    public LocalPhotoSource getPhotoSource() {
        return photoSource;
    }

    public LocalCommentSource getCommentSource() {
        return commentSource;
    }
}
