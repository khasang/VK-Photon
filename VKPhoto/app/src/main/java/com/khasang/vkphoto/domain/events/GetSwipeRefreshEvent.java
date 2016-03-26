package com.khasang.vkphoto.domain.events;

public class GetSwipeRefreshEvent {
    public final boolean refreshing;

    public GetSwipeRefreshEvent (boolean refreshing) {
        this.refreshing = refreshing;
    }
}
