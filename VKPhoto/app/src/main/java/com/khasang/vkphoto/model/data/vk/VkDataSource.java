package com.khasang.vkphoto.model.data.vk;

public class VKDataSource {
    public VKAlbumSource getAlbumSource() {
        return new VKAlbumSource();
    }

    public VKPhotoSource getPhotoSource() {
        return null;
    }

    public VKCommentSource getCommentSource() {
        return null;
    }
}
