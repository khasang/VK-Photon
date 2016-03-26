package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

public class GetLocalAlbumsEvent {
    public final List<PhotoAlbum> albumsList;

    public GetLocalAlbumsEvent(List<PhotoAlbum> albumsList) {
        this.albumsList = albumsList;
    }
}
