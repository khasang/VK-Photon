package com.khasang.vkphoto.presentation.presenter.albums;

public interface VKAlbumsPresenter extends AlbumsPresenter {
    void addAlbum(final String title, final String description, final int privacy, final int commentPrivacy);
    void getAllVKAlbums();
    void editAlbumById(int albumId, String title, String description);
    void editPrivacyAlbumById(int albumId, int privacy);
}
      