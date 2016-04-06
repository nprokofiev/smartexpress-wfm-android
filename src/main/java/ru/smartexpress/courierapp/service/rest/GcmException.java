package ru.smartexpress.courierapp.service.rest;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 05.04.16 16:07
 */
public class GcmException extends RuntimeException {
    public GcmException(Throwable throwable) {
        super(throwable);
    }
}
