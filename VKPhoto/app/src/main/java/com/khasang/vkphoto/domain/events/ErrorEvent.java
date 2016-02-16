package com.khasang.vkphoto.domain.events;

public class ErrorEvent {
    public final String errorMessage;

    public ErrorEvent(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
