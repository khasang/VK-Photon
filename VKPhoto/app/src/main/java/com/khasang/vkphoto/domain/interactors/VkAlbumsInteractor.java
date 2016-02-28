package com.khasang.vkphoto.domain.interactors;

import android.database.Cursor;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.domain.services.SyncServiceImpl;
import com.khasang.vkphoto.presentation.presenter.VKAlbumsPresenterImpl;

/**
 * Интерфейс исполнителя запросов к службе синхронизации.
 * Создается внутри VKAlbumsPresenterImpl
 * @see VKAlbumsPresenterImpl
 * @see SyncServiceImpl
 */
//Todo расширить интерфейс необходимыми методами
public interface VkAlbumsInteractor {
    void syncAlbums(MultiSelector multiSelector,Cursor cursor);

    void getAllAlbums();

    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);

    void deleteVkAlbum(MultiSelector multiSelector, Cursor cursor);
}
      