package com.khasang.vkphoto.presentation.presenter;

import java.util.List;

public interface VKPhotosPresenter extends Presenter {
    void getPhotosByAlbumId(int albumId);

    void deleteSelectedPhoto(List<Integer> selectedPositions);
}
