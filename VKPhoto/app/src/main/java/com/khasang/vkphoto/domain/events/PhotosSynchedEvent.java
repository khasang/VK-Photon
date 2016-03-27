package com.khasang.vkphoto.domain.events;

public class PhotosSynchedEvent {
    public final boolean success;

    public PhotosSynchedEvent(boolean success) {
        this.success = success;
    }
}
      