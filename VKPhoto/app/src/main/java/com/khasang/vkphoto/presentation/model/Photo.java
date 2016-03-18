package com.khasang.vkphoto.presentation.model;

import android.database.Cursor;
import android.os.Parcel;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.khasang.vkphoto.data.database.tables.PhotosTable;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKPhotoSizes;
import org.json.JSONException;
import org.json.JSONObject;

import static com.khasang.vkphoto.data.database.tables.PhotosTable.ALBUM_ID;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.COMMENTS;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.DATE;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.FILE_PATH;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.HEIGHT;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.LIKES;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.OWNER_ID;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.SYNC_STATUS;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.TEXT;
import static com.khasang.vkphoto.data.database.tables.PhotosTable.WIDTH;

public class Photo extends VKApiPhoto {
    public String filePath;
    public int syncStatus;

    public Photo(JSONObject from) throws JSONException {
        super(from);
    }

    public Photo(Cursor cursor, Boolean isAlbumLocal) {
        if (isAlbumLocal){
            this.id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            this.album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            this.date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
            this.filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        else {
            this.id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            this.album_id = cursor.getInt(cursor.getColumnIndex(ALBUM_ID));
            this.owner_id = cursor.getInt(cursor.getColumnIndex(OWNER_ID));
            this.width = cursor.getInt(cursor.getColumnIndex(WIDTH));
            this.height = cursor.getInt(cursor.getColumnIndex(HEIGHT));
            this.text = cursor.getString(cursor.getColumnIndex(TEXT));
            this.date = cursor.getLong(cursor.getColumnIndex(DATE));
            this.likes = cursor.getInt(cursor.getColumnIndex(LIKES));
            this.can_comment = cursor.getInt(cursor.getColumnIndex(PhotosTable.CAN_COMMENT)) == 1;
            this.comments = cursor.getInt(cursor.getColumnIndex(COMMENTS));
            this.filePath = cursor.getString(cursor.getColumnIndex(FILE_PATH));
            this.syncStatus = cursor.getInt(cursor.getColumnIndex(SYNC_STATUS));
        }
    }

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

    public String getUrlToMaxPhoto() {
        if (!TextUtils.isEmpty(photo_2560)) return photo_2560;
        if (!TextUtils.isEmpty(photo_1280)) return photo_1280;
        if (!TextUtils.isEmpty(photo_807)) return photo_807;
        if (!TextUtils.isEmpty(photo_604)) return photo_604;
        if (!TextUtils.isEmpty(photo_130)) return photo_130;
        return photo_75;
    }

    public String getName() {
        char separatorChar = System.getProperty("file.separator", "/").charAt(0);
        String separator = String.valueOf(separatorChar);
        int separatorIndex = filePath.lastIndexOf(separator);
        return (separatorIndex < 0) ? filePath : filePath.substring(separatorIndex + 1, filePath.length());
    }

    public void printPhoto(){
        String formattedDate = DateFormat.format("dd.MM.yyyy hh:mm:ss Z", this.date).toString();
        Logger.d("Photo: "
                + "  id="       + String.valueOf(id)
                + "  album_id=" + String.valueOf(album_id)
                + "  date="     + formattedDate
                + "  filePath=" + filePath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        Photo that = (Photo) o;
        return id == that.id;
    }


    @Override
    public int hashCode() {
        return id;
    }
}
