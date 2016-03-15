package com.khasang.vkphoto.data.vk;

import com.khasang.vkphoto.data.RequestMaker;
import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.util.JsonUtils;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.List;

public class VKCommentSource {

    public void createComment(int photoId, final String message) {
        RequestMaker.createComment(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Logger.d("Сообщение добавлено");
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Logger.d(error.errorMessage);
            }
        }, photoId, message);
    }

    public void updateComment(int comment_id, String newComment) {
        RequestMaker.updateComment(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    if (response.json.getInt("response") == 1) {
                        Logger.d("Сообщение изменено");
                    } else {
                        Logger.d("Ошибка");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Logger.d(error.errorMessage);
            }
        }, comment_id, newComment);
    }

    public void deleteComments(int... commets_id) {
        for (int i = 0; i < commets_id.length; i++) {
            deleteComment(commets_id[i]);
        }
    }

    public void deleteComment(int comment_id) {
        RequestMaker.deleteComment(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    if (response.json.getInt("response") == 1) {
                        Logger.d("Сообщение удалено");
                    } else {
                        Logger.d("Ошибка");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                //Logger.d(error.errorCode);
            }
        }, comment_id);
    }

    public void getCommentsByPhotoId(int photo_id) {
        RequestMaker.getCommentsByPhotoId(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    List<Comment> comments = JsonUtils.getItems(response.json, Comment.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Logger.d(error.errorMessage);
            }
        }, photo_id);
    }

    public void getCommentsByAlbumId(int album_id) {
        RequestMaker.getCommentsByAlbumId(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    List<Comment> comments = JsonUtils.getItems(response.json, Comment.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Logger.d(error.errorMessage);
            }
        }, album_id);
    }

    public void getAllComments() {
        RequestMaker.getAllComments(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    List<Comment> comments = JsonUtils.getItems(response.json, Comment.class);
//                    for (Comment comment : comments) {
//                        Logger.d("text " + comment.text + " likes " + comment.likes);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Logger.d(error.errorMessage);
            }
        });
    }
}
