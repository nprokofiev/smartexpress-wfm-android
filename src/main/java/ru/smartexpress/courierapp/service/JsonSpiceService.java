package ru.smartexpress.courierapp.service;


import android.content.ContextWrapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.service.rest.SeeResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 24.12.14 8:28
 */
public class JsonSpiceService extends JacksonSpringAndroidSpiceService implements Authenticator {



    @Override
    public RestTemplate createRestTemplate() {
       return generateAuthRestTemplate(this);
    }


    public static RestTemplate generateAuthRestTemplate(Authenticator authenticator){

        //find more complete examples in RoboSpice Motivation app
        //to enable Gzip compression and setting request timeouts.

        RestTemplate restTemplate = new RestTemplate();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.authenticator(authenticator);
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.writeTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
        /*HttpLoggingInterceptor log = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logger.info(message);
            }
        });
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addInterceptor(log);*/
            OkHttpClient client = builder.build();
        OkHttp3ClientHttpRequestFactory requestFactory = new OkHttp3ClientHttpRequestFactory(client);


        restTemplate.setRequestFactory(requestFactory);

        restTemplate.setErrorHandler(new SeeResponseErrorHandler());

        /*List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
        interceptors.add(new HeaderRequestInterceptor(MobileConstants.GCM_REGISTRATION_ID, AuthHelper.getGcmRegistrationId(contextWrapper)));
        restTemplate.setInterceptors(interceptors);*/

        // web services support json responses
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StringHttpMessageConverter httpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        httpMessageConverter.setWriteAcceptCharset(false);
        final List<HttpMessageConverter< ? >> listHttpMessageConverters = restTemplate.getMessageConverters();
        listHttpMessageConverters.add(httpMessageConverter);
        listHttpMessageConverters.add( jsonConverter );

        restTemplate.setMessageConverters( listHttpMessageConverters );
        return restTemplate;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        SeUser user = SeUser.current();
        if(user==null)
            return null;
        if (responseCount(response) >= 3) {
            return null; // If we've failed 3 times, give up. - in real life, never give up!!
        }
        Logger.info("user is "+user.getName());
        final String username = user.getPhone();
        final String password = user.getPassword();
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder().header("Authorization", credential).build();
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}