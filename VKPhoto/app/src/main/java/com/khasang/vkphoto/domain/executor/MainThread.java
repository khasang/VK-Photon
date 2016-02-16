package com.khasang.vkphoto.domain.executor;

public interface MainThread {
    void post(final Runnable runnable);
}
