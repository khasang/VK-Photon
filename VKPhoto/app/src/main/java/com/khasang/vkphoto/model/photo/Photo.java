package com.khasang.vkphoto.model.photo;

import android.os.Parcel;

import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoSizes;

public class Photo extends VKApiPhoto {
    public String filePath;
    public int syncStatus;

    /**
     * Creates a Photo instance from Parcel.
     */
    public Photo(Parcel in) {
        this.id = in.readInt();
        this.album_id = in.readInt();
        this.owner_id = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.text = in.readString();
        this.date = in.readLong();
        this.src = in.readParcelable(VKPhotoSizes.class.getClassLoader());
        this.photo_75 = in.readString();
        this.photo_130 = in.readString();
        this.photo_604 = in.readString();
        this.photo_807 = in.readString();
        this.photo_1280 = in.readString();
        this.photo_2560 = in.readString();
        this.user_likes = in.readByte() != 0;
        this.can_comment = in.readByte() != 0;
        this.likes = in.readInt();
        this.comments = in.readInt();
        this.tags = in.readInt();
        this.access_key = in.readString();
        this.filePath = in.readString();
        this.syncStatus = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.album_id);
        dest.writeInt(this.owner_id);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.text);
        dest.writeLong(this.date);
        dest.writeParcelable(this.src, flags);
        dest.writeString(this.photo_75);
        dest.writeString(this.photo_130);
        dest.writeString(this.photo_604);
        dest.writeString(this.photo_807);
        dest.writeString(this.photo_1280);
        dest.writeString(this.photo_2560);
        dest.writeByte(user_likes ? (byte) 1 : (byte) 0);
        dest.writeByte(can_comment ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likes);
        dest.writeInt(this.comments);
        dest.writeInt(this.tags);
        dest.writeString(this.access_key);
        dest.writeString(filePath);
        dest.writeInt(syncStatus);
    }

    public static Creator<Photo> CREATOR = new Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
