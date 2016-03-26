package com.khasang.vkphoto.data;

import android.support.annotation.NonNull;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

import java.io.File;

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

    public static void uploadPhoto(VKRequest.VKRequestListener vkRequestListener, File file, long idVKPhotoAlbum) {
        VKRequest vkRequest = VKApi.uploadAlbumPhotoRequest(file, idVKPhotoAlbum, 0);
        vkRequest.executeWithListener(vkRequestListener);
    }

    public static VKRequest uploadPhotoRequest(File file, long idVKPhotoAlbum) {
        return VKApi.uploadAlbumPhotoRequest(file, idVKPhotoAlbum, 0);
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
        final VKRequest request = getVkRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, albumId,VKApiConst.EXTENDED,1));
        request.executeWithListener(vkRequestListener);
    }

    public static VKRequest getVkPhotosByAlbumIdRequest(int albumId) {
        return getVkRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, albumId));
    }

    public static void getAllVkAlbums(VKRequest.VKRequestListener vkRequestListener) {
        final VKRequest request = getVkRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId,"need_system",1));
        request.executeWithListener(vkRequestListener);
    }

    public static void getPhotoAlbumThumb(VKRequest.VKRequestListener vkRequestListener, PhotoAlbum photoAlbum) {
        VKRequest request = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, photoAlbum.id, VKApiConst.PHOTO_IDS, photoAlbum.thumb_id));
        request.executeSyncWithListener(vkRequestListener);
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
        VKRequest request = new VKRequest("photos.getById", VKParameters.from("photos", VKAccessToken.currentToken().userId + "_" + String.valueOf(photoId),"extended",1));
        request.executeWithListener(vkRequestListener);
    }
    public static void getPhotoByIdSync(VKRequest.VKRequestListener vkRequestListener, int photoId) {
        VKRequest request = new VKRequest("photos.getById", VKParameters.from("photos", VKAccessToken.currentToken().userId + "_" + String.valueOf(photoId),"extended",1));
        request.executeSyncWithListener(vkRequestListener);
    }
    public static void getUserInfoById(VKRequest.VKRequestListener vkRequestListener, int userId) {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.USER_ID, userId, VKApiConst.FIELDS, "photo_50,photo_200,city,verified"));
        request.executeWithListener(vkRequestListener);
    }

    //начало работы с коментариями
    public static void getAllComments(VKRequest.VKRequestListener vkRequestListener){
        VKRequest request = new VKRequest("photos.getAllComments", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, "need_likes", 1));
        request.executeWithListener(vkRequestListener);
    }

    public static void getCommentsByAlbumId(VKRequest.VKRequestListener vkRequestListener, int albumId){
        VKRequest request = new VKRequest("photos.getAllComments", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, VKApiConst.ALBUM_ID,albumId, "need_likes", 1));
        request.executeWithListener(vkRequestListener);
    }

    public static void getCommentsByPhotoId(VKRequest.VKRequestListener vkRequestListener, int photoId){
        VKRequest request = new VKRequest("photos.getComments", VKParameters.from(VKApiConst.OWNER_ID,VKAccessToken.currentToken().userId,"count",100, "photo_id", photoId, "need_likes", 1,"extended",1));
        request.executeWithListener(vkRequestListener);
    }

    public static void deleteComment(VKRequest.VKRequestListener vkRequestListener, int commentId){
        VKRequest request = new VKRequest("photos.deleteComment", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, "comment_id", commentId));
        request.executeWithListener(vkRequestListener);
    }

    public static void updateComment(VKRequest.VKRequestListener vkRequestListener, int commentId, String text){
        VKRequest request = new VKRequest("photos.editComment", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, "comment_id", commentId, VKApiConst.MESSAGE, text));
        request.executeWithListener(vkRequestListener);
    }

    public static void createComment(VKRequest.VKRequestListener vkRequestListener, int photoId, String message){
        VKRequest request = new VKRequest("photos.createComment", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, "photo_id", photoId, VKApiConst.MESSAGE, message));
        request.executeWithListener(vkRequestListener);
    }

    public static void editAlbumById(VKRequest.VKRequestListener vkRequestListener, int albumId, String title, String description){
        VKRequest request = new VKRequest("photos.editAlbum", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, VKApiConst.ALBUM_ID, albumId, "title", title, "description", description));
        request.executeWithListener(vkRequestListener);
    }

    public static void editPrivacyAlbumById(VKRequest.VKRequestListener vkRequestListener, int albumId, int privacy){
        VKRequest request = new VKRequest("photos.editAlbum", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId, VKApiConst.ALBUM_ID, albumId, "privacy", privacy));
        request.executeWithListener(vkRequestListener);
    }

    //конец работы с коментариями

    @NonNull
    private static VKRequest getVkRequest(String apiMethod, VKParameters vkParameters) {
        VKRequest request = new VKRequest(apiMethod, vkParameters);
        request.attempts = ATTEMPTS_COUNT;
        return request;
    }
}
