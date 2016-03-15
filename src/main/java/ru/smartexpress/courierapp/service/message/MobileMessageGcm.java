package ru.smartexpress.courierapp.service.message;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 29.03.15 15:23
 */
public class MobileMessageGcm implements MobileMessageApi {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String TAG = MobileMessageGcm.class.getName();
    @Override
    public String registerDevice(Context context) {
        return null;
    }

    @Override
    public boolean checkServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            String errorMessage;
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                /*GooglePlayServicesUtil.getErrorDialog(resultCode, context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();*/
                errorMessage = GooglePlayServicesUtil.getErrorString(resultCode);
            } else {
                Log.i(TAG, "This device is not supported.");
                errorMessage = "Google play services не обнаружены. Устройство не поддерживается.";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(errorMessage)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog =  builder.create();
            alertDialog.show();
            return false;
        }
        return true;
    }

    @Override
    public void handleIntent(Intent intent) {

    }
}
