package com.khasang.vkphoto.util;

import com.vk.sdk.VKAccessToken;

public class VkAccessTokenHolder {
    private static VKAccessToken vkAccessToken;

    private VkAccessTokenHolder() {
    }

    public static void setVkAccessToken(VKAccessToken vkAccessToken) {
        VkAccessTokenHolder.vkAccessToken = vkAccessToken;
    }

    public static String getUserId() {
        return vkAccessToken.userId;
    }
}
