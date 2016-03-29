package ru.smartexpress.courierapp.service;


import android.content.ContextWrapper;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.DeserializationConfig;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.smartexpress.common.MobileConstants;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.helper.AuthHelper;
import ru.smartexpress.courierapp.service.rest.HeaderRequestInterceptor;
import ru.smartexpress.courierapp.service.rest.SeeResponseErrorHandler;

import java.util.ArrayList;
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
        if(AuthHelper.isLoggedIn(contextWrapper)) {
            String username = AuthHelper.getUsername(contextWrapper);
            String password = AuthHelper.getPassword(contextWrapper);
            setCredentials(restTemplate, username, password);
        }

        restTemplate.setErrorHandler(new SeeResponseErrorHandler());

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
        interceptors.add(new HeaderRequestInterceptor(MobileConstants.GCM_REGISTRATION_ID, AuthHelper.getGcmRegistrationId(contextWrapper)));


        // web services support json responses
        MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
        jsonConverter.getObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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