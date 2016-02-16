package com.khasang.vkphoto.domain;

import android.support.annotation.NonNull;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

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
    public static void createEmptyAlbum(VKRequest.VKRequestListener vkRequestListener, String title,
                                        String description, int privacy, int comment_privacy) {
        final VKRequest request = getVkRequest("photos.createAlbum", VKParameters.from("title", title,
                "description", description, "privacy", privacy, "comment_privacy", comment_privacy));
        request.executeWithListener(vkRequestListener);
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

    public static void getVkAlbum(VKRequest.VKRequestListener vkRequestListener, int albumId) {
        final VKRequest request = getVkRequest("photos.getAlbums", VKParameters.from(VKApiConst.ALBUM_ID, albumId));
        request.executeWithListener(vkRequestListener);
    }

    public static void getAllVkAlbums(VKRequest.VKRequestListener vkRequestListener) {
        final VKRequest request = getVkRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId));
        request.executeWithListener(vkRequestListener);
    }

    @NonNull
    private static VKRequest getVkRequest(String apiMethod, VKParameters vkParameters) {
        VKRequest vkRequest = new VKRequest(apiMethod, vkParameters);
        vkRequest.attempts = ATTEMPTS_COUNT;
        return vkRequest;
    }

}
