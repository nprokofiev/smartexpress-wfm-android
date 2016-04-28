package ru.smartexpress.courierapp.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.receiver.GcmBroadcastReceiver;
import ru.smartexpress.courierapp.service.notification.NotificationProcessor;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 24.01.15 18:03
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MobileMessageIntentService extends GcmListenerService {
    public static final String TAG = "GcmIntentService";
    public static final long MAXIMUM_GCM_PAUSE_SEC = 30L;





    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Logger.info("got gcm message:"+message);
        process();
    }

    private synchronized void process(){
        SeApplication.smartexpress().processNotifications();
    }

}