package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.Photo;

import java.util.List;

/**
 * Created by Anton on 21.02.2016.
 */
public class GetSynchronizedPhotosEvent {
    public final List<Photo> photosList;

    public GetSynchronizedPhotosEvent(List<Photo> photosList) {
        this.photosList = photosList;
    }
}
