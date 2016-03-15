package com.khasang.vkphoto.domain.events;

import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.model.VkProfile;

import java.util.List;

/**
 * Created by admin on 07.03.2016.
 */
public class GetVKCommentsEvent {
    public final List<Comment> commentsList;
    public final List<VkProfile> profiles;

    public GetVKCommentsEvent(List<Comment> albumsList, List<VkProfile> profiles) {
        this.commentsList = albumsList;
        this.profiles = profiles;
    }

}
