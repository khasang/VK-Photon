package com.khasang.vkphoto.domain.tasks;

import android.support.annotation.NonNull;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.data.vk.VKDataSource;
import com.khasang.vkphoto.domain.events.GetVKPhotoEvent;
import com.khasang.vkphoto.presentation.model.MyVkRequestListener;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.util.ErrorUtils;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.Callable;

public class UploadPhotoCallable implements Callable<Photo> {
    File file;
    long idVKPhotoAlbum;
    private VKDataSource vkDataSource;
    private Photo photo = null;

    public UploadPhotoCallable(File file, long idVKPhotoAlbum, VKDataSource vkDataSource) {
        this.vkDataSource = vkDataSource;
        this.file = file;
        this.idVKPhotoAlbum = idVKPhotoAlbum;
    }

    @Override
    public Photo call() throws Exception {
        VKRequest vkRequest = getVKRequest();
        vkRequest.executeSyncWithListener(new MyVkRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    Logger.d("savePhotoToAlbum: " + response.responseString);
                    photo = JsonUtils.getPhoto(response.json, Photo.class);
                    EventBus.getDefault().postSticky(new GetVKPhotoEvent(photo));
                } catch (Exception e) {
                    photo = null;
                    Logger.d(e.toString());
                    sendError(ErrorUtils.JSON_PARSE_FAILED);
                }

            }
        });
        return photo;
    }

    @NonNull
    private VKRequest getVKRequest() {
        VKRequest vkRequest = RequestMaker.uploadPhotoRequest(file, idVKPhotoAlbum);
        vkRequest.attempts = 5;
        return vkRequest;
    }
}
