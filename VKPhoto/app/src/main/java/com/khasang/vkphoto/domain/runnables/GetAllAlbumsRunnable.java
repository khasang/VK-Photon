package com.khasang.vkphoto.domain.runnables;

import com.khasang.vkphoto.domain.listeners.OnGetAllAlbumsListener;
import com.khasang.vkphoto.executor.MainThread;
import com.khasang.vkphoto.util.JsonUtils;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import java.util.List;

public class GetAllAlbumsRunnable implements Runnable {
    private MainThread mainThread;
    private OnGetAllAlbumsListener onGetAllAlbumsListener;

    public GetAllAlbumsRunnable(MainThread mainThread, OnGetAllAlbumsListener onGetAllAlbumsListener) {
        this.mainThread = mainThread;
        this.onGetAllAlbumsListener = onGetAllAlbumsListener;
    }

    @Override
    public void run() {
        final VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
//                    JSONArray jsonArray = JsonUtils.getJsonArray(response.json);
//                    int length = jsonArray.length();
//                    final List<VKApiPhotoAlbum> vkApiPhotoAlbums = new ArrayList<>(length);
//                    for (int i = 0; i < length; i++) {
//                        vkApiPhotoAlbums.add(new VKApiPhotoAlbum(jsonArray.getJSONObject(i)));
//                    }
                    final List<VKApiPhotoAlbum> vkApiPhotoAlbums = JsonUtils.getItems(response.json, VKApiPhotoAlbum.class);
                    if (onGetAllAlbumsListener != null) {
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetAllAlbumsListener.onSuccess(vkApiPhotoAlbums);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final VKError error) {
                super.onError(error);
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        onGetAllAlbumsListener.onVKError(error);
                    }
                });
            }
        });
    }

//    @NonNull
//    private Response<Items<PhotoAlbum>> getAlbumsResponse(VKResponse response) {
//        return new Gson().fromJson(response.json.toString(), new TypeToken<Response<Items<PhotoAlbum>>>() {
//        }.getType());
//    }
}
      