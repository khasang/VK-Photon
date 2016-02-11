package com.khasang.vkphoto.model.data.vk;

import com.khasang.vkphoto.model.data.AlbumSource;
import com.khasang.vkphoto.model.data.CommentSource;
import com.khasang.vkphoto.model.data.DataSource;
import com.khasang.vkphoto.model.data.PhotoSource;

public class VkDataSource implements DataSource {
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
