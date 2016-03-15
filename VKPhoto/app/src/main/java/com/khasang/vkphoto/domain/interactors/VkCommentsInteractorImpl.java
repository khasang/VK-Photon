package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.data.vk.VKCommentSource;
import com.khasang.vkphoto.data.vk.VKPhotoSource;

/**
 * Created by admin on 10.03.2016.
 */
public class VkCommentsInteractorImpl implements VkCommentsInteractor {
    private VKCommentSource vkCommentSource;
    private VKPhotoSource photoSource;
    public VkCommentsInteractorImpl() {
        vkCommentSource = new VKCommentSource();
        photoSource = new VKPhotoSource();
    }

    @Override
    public void getCommentsByPhotoId(int photoId) {
        vkCommentSource.getCommentsByPhotoId(photoId);
    }

    @Override
    public void getPhotoById(int photoId) {
        photoSource.getPhotoById(photoId);
    }
}
