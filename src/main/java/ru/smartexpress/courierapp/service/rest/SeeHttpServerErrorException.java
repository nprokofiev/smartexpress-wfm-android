package ru.smartexpress.courierapp.service.rest;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import ru.smartexpress.common.dto.Error;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 28.03.16 14:59
 */
public class SeeHttpServerErrorException extends HttpServerErrorException {
    private ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public SeeHttpServerErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    public SeeHttpServerErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public SeeHttpServerErrorException(HttpStatus statusCode, String statusText, byte[] responseBody, Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }

    public Error getSEEError(){
        try {
            return objectMapper.readValue(getResponseBodyAsString(), Error.class);
        } catch (IOException e) {
            Error error = new Error();
            error.setErrorMessage("unknownError: " + getMessage());
            return error;
        }

    }


}
