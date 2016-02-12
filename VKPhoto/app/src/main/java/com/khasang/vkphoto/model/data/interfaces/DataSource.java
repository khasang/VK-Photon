package com.khasang.vkphoto.model.data.interfaces;

public interface DataSource {

    AlbumSource getAlbumSource();

    PhotoSource getPhotoSource();

    CommentSource getCommentSource();
}
