package com.khasang.vkphoto.domain;

import android.support.annotation.NonNull;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

public class RequestMaker {
    public static void getAllVkAlbums(VKRequest.VKRequestListener vkRequestListener) {
        final VKRequest request = getVkRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId));
        request.executeWithListener(vkRequestListener);
    }

    @NonNull
    private static VKRequest getVkRequest(String apiMethod, VKParameters vkParameters) {
        VKRequest vkRequest = new VKRequest(apiMethod, vkParameters);
        vkRequest.attempts = 10;
        return vkRequest;
    }
}
