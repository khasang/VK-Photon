package com.khasang.vkphoto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAlbumsResponse {
    public List<PhotoAlbum> results;
    @SerializedName("count")
    public int count;
}
