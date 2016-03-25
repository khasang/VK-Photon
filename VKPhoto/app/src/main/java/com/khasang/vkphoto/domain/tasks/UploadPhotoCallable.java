package com.khasang.vkphoto.domain.tasks;

import android.support.annotation.NonNull;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.vk.VKDataSource;
import com.khasang.vkphoto.data.vk.VKPhotoSource;
import com.khasang.vkphoto.presentation.model.MyVkRequestListener;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by bugtsa on 19-Mar-16.
 */
public class UploadPhotoCallable implements Callable<Boolean> {
    public static final int ATTAMPTS_COUNT = 3;
    File file;
    long idVKPhotoAlbum;
    private VKDataSource vkDataSource;
    private boolean success = false;

    public UploadPhotoCallable(File file, long idVKPhotoAlbum, VKDataSource vkDataSource) {
        this.vkDataSource = vkDataSource;
        this.file = file;
        this.idVKPhotoAlbum = idVKPhotoAlbum;
    }

    @Override
    public Boolean call() throws Exception {
        final VKPhotoSource vkPhotoSource = vkDataSource.getPhotoSource();
        VKRequest vkRequest = getVKRequest();
        vkRequest.executeSyncWithListener(new MyVkRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    Photo photo  = JsonUtils.getPhoto(response.json, Photo.class);
                    success = true;
                    Logger.d("savePhotoToAlbum: " + response.responseString);
                } catch (Exception e) {
                    success = false;
                    Logger.d(e.toString());
                    sendError(ErrorUtils.JSON_PARSE_FAILED);
                }
            }
        });
        return success;
    }

    @NonNull
    private VKRequest getVKRequest() {
        VKRequest vkRequest = RequestMaker.uploadPhotoRequest(file, idVKPhotoAlbum);
        vkRequest.attempts = 5;
        return vkRequest;
    }
}
