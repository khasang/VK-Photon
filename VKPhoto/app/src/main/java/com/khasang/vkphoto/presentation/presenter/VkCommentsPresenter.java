package com.khasang.vkphoto.presentation.presenter;

/**
 * Created by admin on 09.03.2016.
 */
public interface VkCommentsPresenter extends Presenter {
    public void getCommentsByPhotoId(int photoId);

    void getPhotoById(int photoId);

    void registerEventBus();

    void unregisterEventBus();
}
