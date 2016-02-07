package com.khasang.vkphoto;

import android.app.Application;

import com.vk.sdk.VKSdk;

/**
 * Created by aleksandrlihovidov on 07.02.16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(getApplicationContext());
    }
}
