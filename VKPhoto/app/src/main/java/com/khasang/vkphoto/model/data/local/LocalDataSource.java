package com.khasang.vkphoto.model.data.local;

import android.content.ContentResolver;

import com.khasang.vkphoto.model.data.AlbumSource;
import com.khasang.vkphoto.model.data.CommentSource;
import com.khasang.vkphoto.model.data.DataSource;
import com.khasang.vkphoto.model.data.PhotoSource;

public class LocalDataSource implements DataSource {
    private ContentResolver contentResolver;
    private AlbumSource albumSource;
    private PhotoSource photoSource;
    private CommentSource commentSource;

    public LocalDataSource(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public AlbumSource getAlbumSource() {
        return null;
    }

    @Override
    public PhotoSource getPhotoSource() {
        return null;
    }

    @Override
    public CommentSource getCommentSource() {
        return null;
    }
}
