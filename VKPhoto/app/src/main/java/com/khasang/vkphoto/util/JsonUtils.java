package com.khasang.vkphoto.util;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static JSONArray getJsonArray(JSONObject jsonObject) throws JSONException {
        return jsonObject.getJSONObject("response").getJSONArray("items");
    }

    public static JSONObject getJsonObject(JSONObject jsonObject) throws JSONException {
        return jsonObject.getJSONObject("response");
    }

    public static <T> PhotoAlbum getJsonTest(JSONObject jsonObject) throws JSONException {
        PhotoAlbum photoAlbum = null;
        try {
            photoAlbum = new PhotoAlbum(jsonObject.getJSONObject("response"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return photoAlbum;
    }

    public static <T> List<T> getItems(JSONObject jsonObject, Class<T> tClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException {
        JSONArray jsonArray = getJsonArray(jsonObject);
        int length = jsonArray.length();
        final List<T> items = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            try {
                items.add(tClass.getConstructor(JSONObject.class).newInstance(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
}
      