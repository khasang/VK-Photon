package com.khasang.vkphoto.domain.interactors;

import java.util.Vector;

/** Created by bugtsa on 19-Feb-16. */
public interface VkAddAlbumInteractor {
    void addAlbum(final String title, final String description,
                  final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy);
}
