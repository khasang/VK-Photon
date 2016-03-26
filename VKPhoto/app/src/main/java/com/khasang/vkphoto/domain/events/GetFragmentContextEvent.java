package com.khasang.vkphoto.domain.events;

import android.content.Context;

/**
 * Created by Vova on 17.03.2016.
 */
public class GetFragmentContextEvent {
    public Context context;

    public GetFragmentContextEvent(Context context) {
        this.context = context;
    }

}
