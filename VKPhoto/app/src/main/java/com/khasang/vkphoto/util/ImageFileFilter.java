package com.khasang.vkphoto.util;

import com.khasang.vkphoto.presentation.model.Photo;
import java.io.*;

/**
 * A class that implements the Java FileFilter interface.
 */
public class ImageFileFilter implements FileFilter {
    private final String[] okFileExtensions =
            new String[]{"jpg", "png", "gif"};

    public boolean accept(File file) {
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public boolean accept(Photo photo) {
        for (String extension : okFileExtensions)
            if (photo.getName().toLowerCase().endsWith(extension)) return true;
        return false;
    }
}
      