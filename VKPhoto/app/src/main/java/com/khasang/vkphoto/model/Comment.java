package com.khasang.vkphoto.model;


import android.os.Parcel;

import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKAttachments;

import org.json.JSONObject;

public class Comment extends VKApiComment {
    public int photo_id;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.from_id);
        dest.writeLong(this.date);
        dest.writeString(this.text);
        dest.writeInt(this.reply_to_user);
        dest.writeInt(this.reply_to_comment);
        dest.writeInt(this.likes);
        dest.writeByte(user_likes ? (byte) 1 : (byte) 0);
        dest.writeByte(can_like ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.attachments, flags);
        dest.writeInt(this.photo_id);
    }

    public Comment(JSONObject from, int photo_id) {
        super(from);
        this.photo_id = photo_id;
    }

    public Comment(Parcel in) {
        this.id = in.readInt();
        this.from_id = in.readInt();
        this.date = in.readLong();
        this.text = in.readString();
        this.reply_to_user = in.readInt();
        this.reply_to_comment = in.readInt();
        this.likes = in.readInt();
        this.user_likes = in.readByte() != 0;
        this.can_like = in.readByte() != 0;
        this.attachments = in.readParcelable(VKAttachments.class.getClassLoader());
        this.photo_id = in.readInt();
    }

    public static Creator<VKApiComment> CREATOR = new Creator<VKApiComment>() {
        public VKApiComment createFromParcel(Parcel source) {
            return new VKApiComment(source);
        }

        public VKApiComment[] newArray(int size) {
            return new VKApiComment[size];
        }
    };
}
