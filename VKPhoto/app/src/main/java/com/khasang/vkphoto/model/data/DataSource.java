package com.khasang.vkphoto.model.data;

public interface DataSource {

    AlbumSource getAlbumSource();

    PhotoSource getPhotoSource();

    CommentSource getCommentSource();
}
