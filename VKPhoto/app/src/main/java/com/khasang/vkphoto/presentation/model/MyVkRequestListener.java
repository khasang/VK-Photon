package com.khasang.vkphoto.presentation.model;

import com.khasang.vkphoto.domain.events.ErrorEvent;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;

import org.greenrobot.eventbus.EventBus;

public class MyVkRequestListener extends VKRequest.VKRequestListener {
    @Override
    public void onError(VKError error) {
        super.onError(error);
        sendError(error.errorCode);
    }

    public void sendError(int errorCode) {
        EventBus.getDefault().postSticky(new ErrorEvent(errorCode));
    }

}
