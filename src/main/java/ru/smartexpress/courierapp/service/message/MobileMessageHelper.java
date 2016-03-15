package ru.smartexpress.courierapp.service.message;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 29.03.15 15:25
 */
public class MobileMessageHelper {

    public static MobileMessageApi getDefaultImpl(){
        return new MobileMessageGcm();
    }
}
