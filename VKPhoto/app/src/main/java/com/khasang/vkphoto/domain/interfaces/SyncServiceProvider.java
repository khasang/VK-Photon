package com.khasang.vkphoto.domain.interfaces;

import com.khasang.vkphoto.services.SyncService;

/**
 * Интерфейс, который предоставляет ссылку на Bind Service
 *
 * @see com.khasang.vkphoto.ui.activities.MainActivity
 */
public interface SyncServiceProvider {
    SyncService getSyncService();
}
      