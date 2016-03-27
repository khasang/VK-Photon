package com.khasang.vkphoto.presentation.model;

import android.database.Cursor;
import android.os.Parcel;
import android.provider.BaseColumns;

import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.model.VKApiPhotoAlbum;
import com.vk.sdk.api.model.VKPhotoSizes;

import org.json.JSONException;
import org.json.JSONObject;

import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.CAN_UPLOAD;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.CREATED;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.DESCRIPTION;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.FILE_PATH;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.OWNER_ID;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.PRIVACY;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.SIZE;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.SYNC_STATUS;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.THUMB_FILE_PATH;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.THUMB_ID;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.THUMB_SRC;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.TITLE;
import static com.khasang.vkphoto.data.database.tables.PhotoAlbumsTable.UPDATED;

public class PhotoAlbum extends VKApiPhotoAlbum {
    public static Creator<PhotoAlbum> CREATOR = new Creator<PhotoAlbum>() {
        public PhotoAlbum createFromParcel(Parcel source) {
            return new PhotoAlbum(source);
        }

        public PhotoAlbum[] newArray(int size) {
            return new PhotoAlbum[size];
        }
    };
    public String filePath;
    public String thumbFilePath;
    public int syncStatus;

    public PhotoAlbum() {
    }

    public PhotoAlbum(JSONObject from) throws JSONException {
        super(from);
    }

    public PhotoAlbum(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        this.title = cursor.getString(cursor.getColumnIndex(TITLE));
        this.filePath = cursor.getString(cursor.getColumnIndex(FILE_PATH));
        this.thumbFilePath = cursor.getString(cursor.getColumnIndex(THUMB_FILE_PATH));
        this.size = cursor.getInt(cursor.getColumnIndex(SIZE));
        try {
            this.privacy = cursor.getInt(cursor.getColumnIndex(PRIVACY));
            this.description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
            this.owner_id = cursor.getInt(cursor.getColumnIndex(OWNER_ID));
            this.can_upload = cursor.getInt(cursor.getColumnIndex(CAN_UPLOAD)) == 1;
            this.updated = cursor.getLong(cursor.getColumnIndex(UPDATED));
            this.created = cursor.getLong(cursor.getColumnIndex(CREATED));
            this.thumb_id = cursor.getInt(cursor.getColumnIndex(THUMB_ID));
            this.thumb_src = cursor.getString(cursor.getColumnIndex(THUMB_SRC));
            this.syncStatus = cursor.getInt(cursor.getColumnIndex(SYNC_STATUS));
        } catch (Exception e) {
//            printPhotoAlbum();
        }
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
        this.thumbFilePath = in.readString();
        this.syncStatus = in.readInt();
    }

    public PhotoAlbum(VKApiPhotoAlbum vkApiPhotoAlbum) {
        this.id = vkApiPhotoAlbum.id;
        this.title = vkApiPhotoAlbum.title;
        this.size = vkApiPhotoAlbum.size;
        this.privacy = vkApiPhotoAlbum.privacy;
        this.description = vkApiPhotoAlbum.description;
        this.owner_id = vkApiPhotoAlbum.owner_id;
        this.can_upload = vkApiPhotoAlbum.can_upload;
        this.updated = vkApiPhotoAlbum.updated;
        this.created = vkApiPhotoAlbum.created;
        this.thumb_id = vkApiPhotoAlbum.thumb_id;
        this.thumb_src = vkApiPhotoAlbum.thumb_src;
        this.photo = vkApiPhotoAlbum.photo;
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
        dest.writeString(this.thumbFilePath);
        dest.writeInt(this.syncStatus);
    }
    public static boolean checkSelectable(int photoAlbumId) {
        return !(photoAlbumId == -6 || photoAlbumId == -7 || photoAlbumId == -15);
    }
    public void printPhotoAlbum(){
        Logger.d("PhotoAlbum: "
                + "  id="               + String.valueOf(id)
                + "  title="            + title
                + "  filePath="         + filePath
                + "  thumbFilePath="    + thumbFilePath
                + "  size="             + String.valueOf(size));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoAlbum)) return false;
        PhotoAlbum that = (PhotoAlbum) o;
        return id == that.id;
    }


    @Override
    public int hashCode() {
        return id;
    }
}
