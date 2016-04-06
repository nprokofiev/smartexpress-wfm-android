package ru.smartexpress.courierapp.helper;

import java.util.regex.Pattern;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 01.04.16 14:57
 */
public class ValidationHelper {
    public static boolean isPhoneValid(String phone){
        return Pattern.matches("^7[0-9]{10}", phone);
    }
}
