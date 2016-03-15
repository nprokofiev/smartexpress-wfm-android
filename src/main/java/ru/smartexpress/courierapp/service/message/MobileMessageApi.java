package ru.smartexpress.courierapp.service.message;

import android.content.Context;
import android.content.Intent;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 29.03.15 14:47
 */
public interface MobileMessageApi {
    String registerDevice(Context context);
    boolean checkServices(Context context);
    void handleIntent(Intent intent);
}
