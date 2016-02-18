package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

/** Created by bugtsa on 16.02.2016. */
public class GetVkSaveAlbumEvent {
    public final PhotoAlbum photoAlbum;

    public GetVkSaveAlbumEvent(PhotoAlbum photoAlbum) {
        this.photoAlbum = photoAlbum;
    }
}
