package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.List;

/**
 * Created by admin on 07.03.2016.
 */
public class GetVKCommentsEvent {
    public final List<Comment> commentsList;

    public GetVKCommentsEvent(List<Comment> albumsList) {
        this.commentsList = albumsList;
    }
}
