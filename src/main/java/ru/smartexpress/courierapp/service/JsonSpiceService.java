package ru.smartexpress.courierapp.service;


import android.app.Application;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.springandroid.json.jackson.JacksonObjectPersisterFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.activity.LoginActivity;

import java.util.List;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 24.12.14 8:28
 */
public class JsonSpiceService extends JacksonSpringAndroidSpiceService {



    @Override
    public RestTemplate createRestTemplate() {
       return generateAuthRestTemplate(this);
    }


    public static RestTemplate generateAuthRestTemplate(ContextWrapper contextWrapper){
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        //find more complete examples in RoboSpice Motivation app
        //to enable Gzip compression and setting request timeouts.
        SharedPreferences preferences = contextWrapper.getSharedPreferences(LoginActivity.LOGIN_PREFS, 0);
        if(preferences.contains("username")) {
            String username = preferences.getString("username", null);
            String password = preferences.getString("password", null);
            setCredentials(restTemplate, username, password);
        }
        // web services support json responses
        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();

        final List<HttpMessageConverter< ? >> listHttpMessageConverters = restTemplate.getMessageConverters();
        listHttpMessageConverters.add( jsonConverter );

        restTemplate.setMessageConverters( listHttpMessageConverters );
        return restTemplate;
    }

    public static void setCredentials(RestTemplate restTemplate, String username, String password){
        HttpComponentsClientHttpRequestFactory requestFactory =
                (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        DefaultHttpClient httpClient =
                (DefaultHttpClient) requestFactory.getHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(CommonConstants.HOST, CommonConstants.PORT, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(username, password));
    }
}