package com.khasang.vkphoto.model.album;

import android.os.Parcel;

import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKPhotoSizes;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoAlbum extends VKApiPhotoAlbum {
    public String filePath;
    public int syncStatus;


    public PhotoAlbum(JSONObject from) throws JSONException {
        super(from);
    }

    public PhotoAlbum(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.size = in.readInt();
        this.privacy = in.readInt();
        this.description = in.readString();
        this.owner_id = in.readInt();
        this.can_upload = in.readByte() != 0;
        this.updated = in.readLong();
        this.created = in.readLong();
        this.thumb_id = in.readInt();
        this.thumb_src = in.readString();
        this.photo = in.readParcelable(VKPhotoSizes.class.getClassLoader());
        this.filePath = in.readString();
        this.syncStatus = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.size);
        dest.writeInt(this.privacy);
        dest.writeString(this.description);
        dest.writeInt(this.owner_id);
        dest.writeByte(can_upload ? (byte) 1 : (byte) 0);
        dest.writeLong(this.updated);
        dest.writeLong(this.created);
        dest.writeInt(this.thumb_id);
        dest.writeString(this.thumb_src);
        dest.writeParcelable(this.photo, flags);
        dest.writeString(this.filePath);
        dest.writeInt(this.syncStatus);
    }

    public static Creator<PhotoAlbum> CREATOR = new Creator<PhotoAlbum>() {
        public PhotoAlbum createFromParcel(Parcel source) {
            return new PhotoAlbum(source);
        }

        public PhotoAlbum[] newArray(int size) {
            return new PhotoAlbum[size];
        }
    };

}
