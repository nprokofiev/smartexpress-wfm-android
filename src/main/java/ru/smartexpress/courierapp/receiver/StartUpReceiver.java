package ru.smartexpress.courierapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ru.smartexpress.courierapp.core.SmartExpress;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.04.16 11:00
 */
public class StartUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            SmartExpress.onStartUp();
    }
}
