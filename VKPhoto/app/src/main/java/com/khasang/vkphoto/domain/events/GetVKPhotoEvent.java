package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.Photo;

/**
 * Created by admin on 10.03.2016.
 */
public class GetVKPhotoEvent {
    public Photo photo;

    public GetVKPhotoEvent(Photo photo) {
        this.photo = photo;
    }
}
