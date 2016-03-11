package com.khasang.vkphoto.presentation.view;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

/**
 * Created by Anton on 21.02.2016.
 */
public interface VkView {
    void showError(String s);
    void confirmDelete(MultiSelector multiSelector);
}
