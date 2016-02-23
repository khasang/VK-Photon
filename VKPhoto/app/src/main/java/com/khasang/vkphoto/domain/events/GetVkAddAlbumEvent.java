package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

/** Created by bugtsa on 19-Feb-16. */
public class GetVkAddAlbumEvent {
    public final PhotoAlbum photoAlbum;

    public GetVkAddAlbumEvent(PhotoAlbum photoAlbum) {
        this.photoAlbum = photoAlbum;
    }
}
