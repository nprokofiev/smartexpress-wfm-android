package ru.smartexpress.courierapp.request;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.smartexpress.common.dto.LoginRequestDTO;
import ru.smartexpress.common.dto.UserDTO;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.io.IOException;
import java.util.Collections;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 24.12.14 16:48
 */
public class LoginRequest extends SpringAndroidSpiceRequest<UserDTO> {
    private String phone;
    private String password;
    private SharedPreferences preferences;
    private Context context;
    public static final String TAG = "LoginRequest";
    public LoginRequest(String phone, String password, SharedPreferences preferences, Context context) {
        super(UserDTO.class);
        this.phone = phone;
        this.password = password;
        this.preferences = preferences;
        this.context = context;
        setRetryPolicy(new DefaultRetryPolicy(0, 1, 1));
    }

    @Override
    public UserDTO loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpAuthentication authentication = new HttpBasicAuthentication(phone, password);
        headers.setAuthorization(authentication);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String url = CommonConstants.REST_URL+"/courier/login";
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setDeviceId(getDeviceId());
        loginRequestDTO.setRegistrationId(getGcmRegistrationId());
        RestTemplate restTemplate = getRestTemplate();
        JsonSpiceService.setCredentials(restTemplate, phone, password);
        return restTemplate.postForObject(url, loginRequestDTO, UserDTO.class);
//        ResponseEntity<UserDTO> response = getRestTemplate().exchange(url, HttpMethod.POST, new HttpEntity<Object>(headers), UserDTO.class);
//        return response.getBody();
    }

    private String getGcmRegistrationId() throws IOException{
        String registrationId = preferences.getString(CommonConstants.REGISTRATION_ID_KEY, null);
        if(registrationId == null) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            registrationId = gcm.register(CommonConstants.SENDER_ID);
            Log.i(TAG, "got registrationId: "+registrationId);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(CommonConstants.REGISTRATION_ID_KEY, registrationId);
            editor.commit();
        }
        return registrationId;
    }

    private String getDeviceId(){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
