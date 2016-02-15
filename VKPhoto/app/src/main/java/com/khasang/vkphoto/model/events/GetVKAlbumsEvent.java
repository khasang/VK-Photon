package com.khasang.vkphoto.model.events;

import com.khasang.vkphoto.model.PhotoAlbum;

import java.util.List;

public class GetVKAlbumsEvent {
    public final List<PhotoAlbum> albumsList;

    public GetVKAlbumsEvent(List<PhotoAlbum> albumsList) {
        this.albumsList = albumsList;
    }
}
