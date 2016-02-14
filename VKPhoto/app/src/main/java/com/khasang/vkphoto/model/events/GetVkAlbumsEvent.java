package com.khasang.vkphoto.model.events;

import com.khasang.vkphoto.model.PhotoAlbum;

import java.util.List;

public class GetVkAlbumsEvent {
    public final List<PhotoAlbum> albumsList;

    public GetVkAlbumsEvent(List<PhotoAlbum> albumsList) {
        this.albumsList = albumsList;
    }
}
