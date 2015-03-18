package ru.smartexpress.courierapp.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import ru.smartexpress.courierapp.activity.MainActivity;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 17.03.15 8:06
 */
public class SystemHelper {
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void sendUpdateUI(Context context){
        Intent updateMainActivity = new Intent();
        updateMainActivity.setAction(MainActivity.UPDATE_CONTENT_ACTION);
        context.sendBroadcast(updateMainActivity);
    }
}
