package com.khasang.vkphoto.domain.events;

import android.content.Context;

/**
 * Created by Vova on 17.03.2016.
 */
public class GotoBackFragmentEvent {
    public Context context;

    public GotoBackFragmentEvent(Context context) {
        this.context = context;
    }
}
