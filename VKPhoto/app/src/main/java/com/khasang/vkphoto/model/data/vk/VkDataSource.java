package com.khasang.vkphoto.model.data.vk;

import com.khasang.vkphoto.model.data.interfaces.AlbumSource;
import com.khasang.vkphoto.model.data.interfaces.CommentSource;
import com.khasang.vkphoto.model.data.interfaces.DataSource;
import com.khasang.vkphoto.model.data.interfaces.PhotoSource;

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
