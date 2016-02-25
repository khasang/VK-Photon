package com.khasang.vkphoto.domain.interactors;

/** Created by bugtsa on 19-Feb-16. */
public interface VkAddAlbumInteractor {
    void addAlbum(final String title, final String description,
                  final int privacy, final int commentPrivacy);
}
