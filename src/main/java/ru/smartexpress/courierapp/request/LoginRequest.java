package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import org.springframework.http.*;
import ru.smartexpress.common.dto.UserDTO;
import ru.smartexpress.courierapp.CommonConstants;

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


    public LoginRequest(String phone, String password) {
        super(UserDTO.class);
        this.phone = phone;
        this.password = password;
        setRetryPolicy(new DefaultRetryPolicy(0, 1, 1));
    }

    @Override
    public UserDTO loadDataFromNetwork() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpAuthentication authentication = new HttpBasicAuthentication(phone, password);
        headers.setAuthorization(authentication);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String url = CommonConstants.REST_URL+"/courier/testLogin";
        ResponseEntity<UserDTO> response = getRestTemplate().exchange(url, HttpMethod.POST, new HttpEntity<Object>(headers), UserDTO.class);
        return response.getBody();
    }
}
