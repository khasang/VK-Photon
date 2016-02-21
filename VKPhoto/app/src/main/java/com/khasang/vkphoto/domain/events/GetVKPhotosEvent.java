package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.Photo;

import java.util.List;

/**
 * Created by Anton on 21.02.2016.
 */
public class GetVKPhotosEvent {
    public final List<Photo> photosList;

    public GetVKPhotosEvent(List<Photo> photosList) {
        this.photosList = photosList;
    }
}
