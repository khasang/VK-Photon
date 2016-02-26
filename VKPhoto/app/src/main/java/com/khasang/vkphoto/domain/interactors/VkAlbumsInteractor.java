package com.khasang.vkphoto.domain.interactors;

import android.database.Cursor;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenterImpl;

import java.util.concurrent.ExecutorService;

/**
 * Интерфейс исполнителя запросов к службе синхронизации.
 * Создается внутри VKAlbumsPresenterImpl
 *
 * @see VKAlbumsPresenterImpl
 * @see SyncServiceImpl
 */
//Todo расширить интерфейс необходимыми методами
public interface VkAlbumsInteractor {
    void syncAlbums(MultiSelector multiSelector, Cursor cursor);

    void getAllAlbums();

    void downloadAlbumThumb(final LocalPhotoSource localPhotoSource, final PhotoAlbum photoAlbum, final ExecutorService executor);

    void deleteVkAlbum(MultiSelector multiSelector, Cursor cursor);
}
      