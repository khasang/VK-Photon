package com.khasang.vkphoto.domain.interfaces;

import com.khasang.vkphoto.domain.services.SyncService;

/**
 * Интерфейс, который предоставляет ссылку на Bind Service
 *
 * @see com.khasang.vkphoto.presentation.activities.MainActivity
 */
public interface SyncServiceProvider {
    SyncService getSyncService();
}
      