package com.khasang.vkphoto.domain.interactors;

import com.khasang.vkphoto.data.vk.VKCommentSource;

/**
 * Created by admin on 10.03.2016.
 */
public class VkCommentsInteractorImpl implements VkCommentsInteractor {
    private VKCommentSource vkCommentSource;
    public VkCommentsInteractorImpl() {
        vkCommentSource = new VKCommentSource();
    }

    @Override
    public void getCommentsByPhotoId(int photoId) {
        vkCommentSource.getCommentsByPhotoId(photoId);
    }
}
