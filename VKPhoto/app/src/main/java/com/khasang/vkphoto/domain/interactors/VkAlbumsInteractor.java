package com.khasang.vkphoto.domain.interactors;

import android.database.Cursor;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.albums.AlbumsPresenterImpl;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Интерфейс исполнителя запросов к службе синхронизации.
 * Создается внутри AlbumsPresenterImpl
 *
 * @see AlbumsPresenterImpl
 * @see SyncServiceImpl
 */
//Todo расширить интерфейс необходимыми методами
public interface VkAlbumsInteractor {
    void syncAlbums(MultiSelector multiSelector, Cursor cursor);

    void getAllAlbums();

    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);

    File downloadAlbumThumb(final LocalPhotoSource localPhotoSource, final PhotoAlbum photoAlbum, final ExecutorService executor);

    void deleteVkAlbum(MultiSelector multiSelector, Cursor cursor);

    void cancelAlbumsSync(List<PhotoAlbum> selectedAlbums);

    void editAlbum(int albumId, String title, String description);

    void editPrivacyAlbum(int albumId, int privacy);
}