package com.khasang.vkphoto.model.data.interfaces;

public interface CommentSource {
    void saveComment();

    void updateComment();

    void deleteComment();

    void deleteComments();

    void getCommentByPhotoId();

    void getCommentsByAlbumId();

    void getAllComments();
}
