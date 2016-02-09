package com.khasang.vkphoto.model.photo;

import com.google.gson.annotations.SerializedName;

public class Photo {
    /**
     * Photo ID, positive number
     */
    @SerializedName("id")
    public int id;

    /**
     * Photo album ID.
     */
    @SerializedName("album_id")
    public int album_id;

    /**
     * ID of the user or community that owns the photo.
     */
    @SerializedName("owner_id")
    public int owner_id;

    /**
     * Width (in pixels) of the original photo.
     */
    @SerializedName("width")
    public int width;

    /**
     * Height (in pixels) of the original photo.
     */
    @SerializedName("height")
    public int height;

    /**
     * Text describing the photo.
     */
    @SerializedName("text")
    public String text;

    /**
     * Date (in Unix time) the photo was added.
     */
    @SerializedName("date")
    public long date;

    /**
     * URL of image with maximum size 75x75px.
     */
    @SerializedName("photo_75")
    public String photo_75;

    /**
     * URL of image with maximum size 130x130px.
     */
    @SerializedName("photo_130")
    public String photo_130;

    /**
     * URL of image with maximum size 604x604px.
     */
    @SerializedName("photo_604")
    public String photo_604;

    /**
     * URL of image with maximum size 807x807px.
     */
    @SerializedName("photo_807")
    public String photo_807;

    /**
     * URL of image with maximum size 1280x1024px.
     */
    @SerializedName("photo_1280")
    public String photo_1280;

    /**
     * URL of image with maximum size 2560x2048px.
     */
    @SerializedName("photo_2560")
    public String photo_2560;

    /**
     * All photo thumbs in photo sizes.
     * It has data even if server returned them without {@code PhotoSizes} format.
     */

    /**
     * Information whether the current user liked the photo.
     */
    public boolean user_likes;

    /**
     * Whether the current user can comment on the photo
     */
    public boolean can_comment;

    /**
     * Number of likes on the photo.
     */
    public int likes;

    /**
     * Number of comments on the photo.
     */
    public int comments;

    /**
     * Number of tags on the photo.
     */
    public int tags;

    /**
     * An access key using for get information about hidden objects.
     */
    public String access_key;
}
