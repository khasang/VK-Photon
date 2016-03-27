package com.khasang.vkphoto.domain.events;

import android.content.Context;

public class GotoBackFragmentEvent {
    public Context context;

    public GotoBackFragmentEvent(Context context) {
        this.context = context;
    }
}
