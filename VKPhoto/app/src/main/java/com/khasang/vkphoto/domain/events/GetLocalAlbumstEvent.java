package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

public class GetLocalAlbumstEvent {
    public final List<PhotoAlbum> albumsList;

    public GetLocalAlbumstEvent(List<PhotoAlbum> albumsList) {
        this.albumsList = albumsList;
    }
}
