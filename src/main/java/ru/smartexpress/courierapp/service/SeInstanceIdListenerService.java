package ru.smartexpress.courierapp.service;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.dto.UserDTO;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.request.LoginRequest;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 05.04.16 14:58
 */
public class SeInstanceIdListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";
    private SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        /*// Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);*/
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
    }
    // [END refresh_token]
}
