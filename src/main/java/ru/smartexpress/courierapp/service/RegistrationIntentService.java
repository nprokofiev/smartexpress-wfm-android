package ru.smartexpress.courierapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import org.springframework.web.client.RestTemplate;
import ru.smartexpress.common.dto.GcmTokenUpdate;
import ru.smartexpress.common.dto.UserDTO;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.request.GcmTokenUpdateRequest;
import ru.smartexpress.courierapp.request.LoginRequest;

import java.util.concurrent.CountDownLatch;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 05.04.16 13:03
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "SmartExpress";
    private static final String[] TOPICS = {"global"};

    public static final String REGISTRATION_COMPLETE = "ru.smartexpress.event.GcmRegistrationComplete";
    public static final String TOKEN_SENT_TO_SERVER = "tokenSentToServer";
    private SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(!spiceManager.isStarted())
            spiceManager.start(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]

            // [END get_token]
            spiceManager.execute(new LoginRequest(SeUser.current()), new RequestListener<UserDTO>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Logger.info("failed to update gcm token");
                }

                @Override
                public void onRequestSuccess(UserDTO userDTO) {
                    Logger.info("gcm token updated ok");
                }
            });


            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(TOKEN_SENT_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Logger.error(e, "Failed to complete token refresh");
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(TOKEN_SENT_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    @Override
    public void onDestroy() {
        spiceManager.shouldStop();
        super.onDestroy();

    }


}
