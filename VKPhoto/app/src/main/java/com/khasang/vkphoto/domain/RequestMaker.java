package com.khasang.vkphoto.domain;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

public class RequestMaker {
    public static void getAllVkAlbums(VKRequest.VKRequestListener vkRequestListener) {
        final VKRequest request = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKAccessToken.currentToken().userId));
        request.executeWithListener(vkRequestListener);
    }
}
