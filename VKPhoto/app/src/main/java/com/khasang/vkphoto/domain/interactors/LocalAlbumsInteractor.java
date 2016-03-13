package com.khasang.vkphoto.domain.interactors;

import android.content.Context;
import android.database.Cursor;
import com.bignerdranch.android.multiselector.MultiSelector;

/**
 * Created by TAU on 07.03.2016.
 */
public interface LocalAlbumsInteractor {
    void syncLocalAlbums(MultiSelector multiSelector, Cursor cursor);
    void addAlbum(final String title, final String description);
    void deleteSelectedLocalAlbums(MultiSelector multiSelector, Cursor cursor, Context context);
}
