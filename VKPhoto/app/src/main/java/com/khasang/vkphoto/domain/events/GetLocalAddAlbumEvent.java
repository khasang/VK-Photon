package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

/** Created by bugtsa on 19-Feb-16. */
public class GetLocalAddAlbumEvent {
    public PhotoAlbum photoAlbum;

    public GetLocalAddAlbumEvent(PhotoAlbum photoAlbum) {
        this.photoAlbum = photoAlbum;
    }
}
