package com.khasang.vkphoto.model.album;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoAlbums {
    @SerializedName("items")
    public List<PhotoAlbum> results;
    @SerializedName("count")
    public int count;
}
