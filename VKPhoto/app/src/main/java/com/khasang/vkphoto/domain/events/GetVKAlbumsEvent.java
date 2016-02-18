package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

public class GetVKAlbumsEvent {
    public final List<PhotoAlbum> albumsList;

    public GetVKAlbumsEvent(List<PhotoAlbum> albumsList) {
        this.albumsList = albumsList;
    }
}
