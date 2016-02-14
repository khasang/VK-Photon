package com.khasang.vkphoto.model.events;

import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.List;

public class GetVkAlbumsEvent {
    public final List<VKApiPhotoAlbum> albumsList;

    public GetVkAlbumsEvent(List<VKApiPhotoAlbum> albumsList) {
        this.albumsList = albumsList;
    }
}
