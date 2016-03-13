package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.Photo;

import java.util.List;

/**
 * Created by Anton on 13.03.2016.
 */
public class GetLocalPhotosEvent {
    public final List<Photo> photosList;

    public GetLocalPhotosEvent(List<Photo> photosList) {
        this.photosList = photosList;
    }
}
