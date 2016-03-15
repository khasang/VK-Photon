package com.khasang.vkphoto.presentation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 08.03.2016.
 * Используется для коментариев из ВК
 */
public class VkProfile implements android.os.Parcelable{

    private final String ID = "id";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";
    private final String SEX = "sex";
    private final String SCREEN_NAME = "screen_name";
    private final String PHOTO_50 = "photo_50";
    private final String PHOTO_100 = "photo_100";
    private final String ONLINE = "online";

    public int id;
    public String first_name;
    public String last_name;
    public int sex;
    public String screen_name;
    public String photo_50;
    public String photo_100;
    public int online;

    public VkProfile(JSONObject jsonObject){
       this.id = jsonObject.optInt(ID);
       this.first_name = jsonObject.optString(FIRST_NAME);
       this.last_name = jsonObject.optString(LAST_NAME);
       this.sex = jsonObject.optInt(SEX);
       this.screen_name = jsonObject.optString(SCREEN_NAME);
       this.photo_50 = jsonObject.optString(PHOTO_50);
       this.photo_100 = jsonObject.optString(PHOTO_100);
       this.online = jsonObject.optInt(ONLINE);
    }

    public VkProfile(Parcel in){
       this.id = in.readInt();
       this.first_name = in.readString();
       this.last_name = in.readString();
       this.sex = in.readInt();
       this.screen_name = in.readString();
       this.photo_50 = in.readString();
       this.photo_100 = in.readString();
       this.online = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeInt(this.sex);
        dest.writeString(this.screen_name);
        dest.writeString(this.photo_50);
        dest.writeString(this.photo_100);
        dest.writeInt(this.online);
    }
}
