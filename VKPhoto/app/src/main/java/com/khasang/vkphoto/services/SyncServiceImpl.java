package com.khasang.vkphoto.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.khasang.vkphoto.domain.interfaces.OnGetAllAlbumsListener;
import com.khasang.vkphoto.executor.MainThread;
import com.khasang.vkphoto.executor.MainThreadImpl;
import com.khasang.vkphoto.executor.ThreadExecutor;
import com.khasang.vkphoto.model.album.GetAlbumsResponse;
import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.model.album.PhotoAlbum;
import com.khasang.vkphoto.util.VkAccessTokenHolder;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

public class SyncServiceImpl extends Service implements SyncService {
    public static final String TAG = SyncService.class.getSimpleName();
    private ThreadExecutor executor = new ThreadExecutor();
    private MainThread mainThread = new MainThreadImpl();
    private MyBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void getAllAlbums(final OnGetAllAlbumsListener onGetAllAlbumsListener) {
//        VKRequest request = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, VkAccessTokenHolder.getUserId(), VKApiConst.MESSAGE, "Привет, друзья!"));
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VkAccessTokenHolder.getUserId()));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Gson gson = new Gson();
                GetAlbumsResponse albumsResponse = gson.fromJson(response.json.toString(), GetAlbumsResponse.class);
                if (onGetAllAlbumsListener != null) {
                    onGetAllAlbumsListener.onSuccess(albumsResponse.photoAlbums.results);
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                onGetAllAlbumsListener.onVKError(error);
            }
        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
////                try {
////                    TimeUnit.SECONDS.sleep(1);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                mainThread.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        onGetAllAlbumsListener.onVKError(new RuntimeException("Gotcha! Exception"));
//                    }
//                });
//            }
//        });

    }

    @Override
    public boolean changeAlbumPrivacy(int i) {
        return false;
    }

    @Override
    public void syncAlbum(PhotoAlbum photoAlbum) {

    }

    @Override
    public Photo getPhoto() {
        return null;
    }

    @Override
    public PhotoAlbum createAlbum() {
        return null;
    }

    public class MyBinder extends Binder {
        public SyncService getService() {
            return SyncServiceImpl.this;
        }
    }
}