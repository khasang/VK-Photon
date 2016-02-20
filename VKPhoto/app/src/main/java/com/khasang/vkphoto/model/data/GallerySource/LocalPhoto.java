package com.khasang.vkphoto.model.data.GallerySource;

/**
 * Created by admin on 20.02.2016.
 */
public class LocalPhoto {
    private String path;
    private String name;

    public LocalPhoto(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
