package ru.smartexpress.courierapp.order;

import android.content.Context;
import android.content.SharedPreferences;
import ru.smartexpress.courierapp.activity.LoginActivity;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.03.15 21:11
 */
public class UserDAO {
    public static final String MESSAGE_ID_OFFSET = "messageIdOffset";
    public static long getLastMessageOffset(Context context){
        SharedPreferences preferences = context.getSharedPreferences(LoginActivity.LOGIN_PREFS, 0);
        return preferences.getLong(MESSAGE_ID_OFFSET, 0);
    }

    public static void setLastMessageOffset(Context context, long offset){
        SharedPreferences preferences = context.getSharedPreferences(LoginActivity.LOGIN_PREFS, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(MESSAGE_ID_OFFSET, offset);
        editor.commit();

    }
}
