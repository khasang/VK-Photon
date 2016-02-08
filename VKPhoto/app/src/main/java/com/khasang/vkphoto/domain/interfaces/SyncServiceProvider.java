package com.khasang.vkphoto.domain.interfaces;

import com.khasang.vkphoto.services.SyncService;

public interface SyncServiceProvider {
    SyncService getSyncService();
}
      