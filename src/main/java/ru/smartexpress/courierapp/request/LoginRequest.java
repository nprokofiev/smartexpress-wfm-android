package ru.smartexpress.courierapp.request;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import okhttp3.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.smartexpress.common.dto.LoginRequestDTO;
import ru.smartexpress.common.dto.UserDTO;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.core.SmartExpress;
import ru.smartexpress.courierapp.helper.AuthHelper;
import ru.smartexpress.courierapp.service.JsonSpiceService;
import ru.smartexpress.courierapp.service.rest.AuthenticationException;
import ru.smartexpress.courierapp.service.rest.GcmException;
import ru.smartexpress.courierapp.service.rest.SeeHttpServerErrorException;

import java.io.IOException;
import java.util.Collections;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 24.12.14 16:48
 */
public class LoginRequest extends AbstractSpringAndroidRequest<UserDTO> implements Authenticator {

    private SeUser user;

    private int mCount;

    public LoginRequest(SeUser user) {
        super(UserDTO.class);
        this.user = user;

        //this.context = context;
        setRetryPolicy(new DefaultRetryPolicy(0, 1, 1));
    }

    @Override
    public UserDTO loadDataFromNetwork() throws Exception {

        String url = baseUrl+"/courier/login";
        Logger.info("executing login request on:"+url);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
   //     loginRequestDTO.setDeviceId(getDeviceId());

        loginRequestDTO.setRegistrationId(getGcmRegistrationId());
        RestTemplate restTemplate = getRestTemplate();
        return restTemplate.postForObject(url, loginRequestDTO, UserDTO.class);
//        ResponseEntity<UserDTO> response = getRestTemplate().exchange(url, HttpMethod.POST, new HttpEntity<Object>(headers), UserDTO.class);
//        return response.getBody();
    }

    private String getGcmRegistrationId() throws IOException{
        try {
            Context context = SeApplication.app();
            InstanceID instanceID = InstanceID.getInstance(context);
            return instanceID.getToken(SeApplication.smartexpress().config.senderId,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        }
        catch (Exception e){
            throw new GcmException(e);
        }
    }


    @Override
    public RestTemplate getRestTemplate() {
        return JsonSpiceService.generateAuthRestTemplate(this);
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if(mCount++ >0)
            throw new AuthenticationException();
        Logger.info("user is "+user.getName());
        final String username = user.getPhone();
        final String password = user.getPassword();
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder().header("Authorization", credential).build();
    }


}
