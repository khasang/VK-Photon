package com.khasang.vkphoto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoAlbum {
    //    id: 112120371,
//    thumb_id: 169255459,
//    owner_id: 9414475,
//    title: 'Крокодил - рисуй и угадывай',
//    description: 'Рисунки из игры Крокодил - http://kroko.vkontakte.ru/',
//    created: 1276887693,
//    updated: 1277314548,
//    size: 2,
//    thumb_is_last: 1,
//    privacy_view: ['all'],
//    privacy_comment: ['all']
    @SerializedName("id")
    public int id;

    @SerializedName("thumb_id")
    public int thumb_id;
    @SerializedName("title")
    public String title;
    @SerializedName("description")
    public String description;
    @SerializedName("created")
    public long created;
    @SerializedName("updated")
    public long updated;
    @SerializedName("size")
    public int size;

    private List<Photo> photos;

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
