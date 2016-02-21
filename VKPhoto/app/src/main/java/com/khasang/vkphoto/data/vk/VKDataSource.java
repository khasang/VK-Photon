package com.khasang.vkphoto.data.vk;

public class VKDataSource {
    public VKAlbumSource getAlbumSource() {
        return new VKAlbumSource();
    }

    public VKPhotoSource getPhotoSource() {
        return new VKPhotoSource();
    }

    public VKCommentSource getCommentSource() {
        return new VKCommentSource();
    }
}
