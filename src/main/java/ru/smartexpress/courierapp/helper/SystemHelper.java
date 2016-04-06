package ru.smartexpress.courierapp.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.activity.MainActivity;


/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 17.03.15 8:06
 */
public class SystemHelper {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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

    public static boolean isUiThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean checkPlayServices(Activity context){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(context, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(context, context.getString(R.string.bad_device), Toast.LENGTH_LONG);
                context.finish();
            }
            return false;
        }
        return true;
    }
}
