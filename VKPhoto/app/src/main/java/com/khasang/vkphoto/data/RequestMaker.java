package com.khasang.vkphoto.data;

import android.support.annotation.NonNull;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.khasang.vkphoto.domain.events.GetVkAddAlbumEvent;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Vector;

public class RequestMaker {

    public static final int ATTEMPTS_COUNT = 10;

    /**
     * Создаёт пустой альбом (VK API 3.0)
     *
     * @param vkRequestListener слушатель запроса
     * @param title             название альбома
     * @param description       текст описания альбома
     * @param privacy           настройки приватности просмотра альбома
     * @param comment_privacy   настройки приватности комментирования
     */
    public void requestCreateEmptyAlbum(VKRequest.VKRequestListener vkRequestListener, String title,
                                        String description, int privacy, int comment_privacy) {
        final VKRequest request = getVkRequest("photos.createAlbum", VKParameters.from("title", title,
                "description", description, "privacy", privacy, "comment_privacy", comment_privacy));
        request.executeWithListener(vkRequestListener);
    }

    /**
     * Добавляет альбом на сервере VK
     * @param title
     * @param description
     * @param listUploadedFiles
     * @param privacy
     * @param comment_privacy
     */
    public void requestAddAlbum(final String title, final String description,
                                final Vector<String> listUploadedFiles, final int privacy, final int comment_privacy) {
        requestCreateEmptyAlbum(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                final PhotoAlbum photoAlbum;
                try {
                    photoAlbum = JsonUtils.getJsonObject(response.json, PhotoAlbum.class);
                    Logger.d("Add Album successfully");
                    EventBus.getDefault().postSticky(new GetVkAddAlbumEvent(photoAlbum));
                } catch (Exception e) {
                    sendError(e.toString());
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                sendError(error.toString());
            }

            void sendError(String s) {
                EventBus.getDefault().postSticky(new ErrorEvent(s));
            }
        }, title, description, privacy, comment_privacy);
    }

    public static void uploadPhoto(File file, PhotoAlbum photoAlbum, VKRequest.VKRequestListener vkRequestListener) {
        VKRequest vkRequest = VKApi.uploadAlbumPhotoRequest(file, photoAlbum.id, 0);
        vkRequest.executeWithListener(vkRequestListener);
    }

    public static void getUploadServer(VKRequest.VKRequestListener vkRequestListener, int albumId) {
        final VKRequest request = getVkRequest("photos.getUploadServer", VKParameters.from(VKApiConst.ALBUM_ID, albumId));
        request.executeWithListener(vkRequestListener);
    }

    public static void VkSave(VKRequest.VKRequestListener vkRequestListener, int albumId,
                              int server, String photosList, String hash) {
        final VKRequest request = getVkRequest("photos.save", VKParameters.from(VKApiConst.ALBUM_ID, albumId,
                "server", server, "photos_list", photosList, "hash", hash));
        request.executeWithListener(vkRequestListener);
    }

    public static void getVkPhotosByAlbumId(VKRequest.VKRequestListener vkRequestListener, int albumId) {
        final VKRequest request = getVkRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, albumId));
        request.executeWithListener(vkRequestListener);
    }

    public static void getAllVkAlbums(VKRequest.VKRequestListener vkRequestListener) {
        final VKRequest request = getVkRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId));
        request.executeWithListener(vkRequestListener);
    }

    public static void getPhotoAlbumThumb(VKRequest.VKRequestListener vkRequestListener, PhotoAlbum photoAlbum) {
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.ALBUM_ID, photoAlbum.id, VKApiConst.PHOTO_IDS, photoAlbum.thumb_id));
        request.executeWithListener(vkRequestListener);
    }

    public static void deleteVkPhotoById(VKRequest.VKRequestListener vkRequestListener, int photoId) {
        final VKRequest request = getVkRequest("photos.delete", VKParameters.from("photo_id", photoId, VKAccessToken.currentToken().userId));
        request.executeWithListener(vkRequestListener);
    }

    public static void deleteVkAlbumById(VKRequest.VKRequestListener vkRequestListener, int albumId) {
        final VKRequest request = getVkRequest("photos.deleteAlbum", VKParameters.from(VKApiConst.ALBUM_ID, albumId, VKAccessToken.currentToken().userId));
        request.executeWithListener(vkRequestListener);
    }

    public static void getPhotoById(VKRequest.VKRequestListener vkRequestListener, int photoId) {
        VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from("photos", VKAccessToken.currentToken().userId + "_" + String.valueOf(photoId)));
        request.executeWithListener(vkRequestListener);
    }


    @NonNull
    private static VKRequest getVkRequest(String apiMethod, VKParameters vkParameters) {
        VKRequest request = new VKRequest(apiMethod, vkParameters);
        request.attempts = ATTEMPTS_COUNT;
        return request;
    }

}
