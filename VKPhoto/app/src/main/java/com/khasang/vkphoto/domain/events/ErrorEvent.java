package com.khasang.vkphoto.domain.events;

public class ErrorEvent {
    public final int errorCode;

    public ErrorEvent(int errorCode) {
        this.errorCode = errorCode;
    }
}
