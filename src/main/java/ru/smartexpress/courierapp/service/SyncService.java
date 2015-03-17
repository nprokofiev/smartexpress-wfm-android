package ru.smartexpress.courierapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 14.03.15 6:39
 */
public class SyncService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
