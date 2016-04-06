package ru.smartexpress.courierapp.helper;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import ru.smartexpress.common.dto.UserDTO;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.activity.LoginActivity;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 28.03.16 11:08
 */
public class AuthHelper {
    public static final String IS_LOGGED_IN = "loggedIn";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String GCM_REG_KEY = "registrationId";

    public static boolean isLoggedIn(ContextWrapper contextWrapper){
        return getPrefs(contextWrapper).getBoolean(IS_LOGGED_IN, false);
    }

    public static String getUsername(ContextWrapper contextWrapper){
        return getPrefs(contextWrapper).getString(USERNAME, null);
    }

    public static String getPassword(ContextWrapper contextWrapper){
        return getPrefs(contextWrapper).getString(PASSWORD, null);
    }

    public static String getGcmRegistrationId(ContextWrapper contextWrapper){
        return getPrefs(contextWrapper).getString(CommonConstants.REGISTRATION_ID_KEY, null);
    }

    public static void setGcmRegistrationId(String gcmRegistrationId, ContextWrapper contextWrapper){
        getPrefs(contextWrapper).edit().putString(GCM_REG_KEY, gcmRegistrationId).commit();
    }

    public static SharedPreferences getPrefs(ContextWrapper contextWrapper){
        return contextWrapper.getSharedPreferences(LoginActivity.LOGIN_PREFS, 0);
    }

    public static void login(String username, String password, ContextWrapper contextWrapper){
        SharedPreferences.Editor editor = getPrefs(contextWrapper).edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.commit();
    }

    public static void setLoginCredentials(String username, String password, ContextWrapper contextWrapper){
        SharedPreferences.Editor editor = getPrefs(contextWrapper).edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.commit();
    }
    public static void forceLogout(ContextWrapper context){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(IS_LOGGED_IN, false);
        if(SystemHelper.isUiThread()){
            Intent intent = new Intent(context,
                    LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
            if(context instanceof Activity)
                ((Activity) context).finish();
        }

    }
}
