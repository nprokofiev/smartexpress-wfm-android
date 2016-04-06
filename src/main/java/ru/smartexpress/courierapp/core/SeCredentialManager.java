package ru.smartexpress.courierapp.core;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 30.03.16 12:35
 */
public class SeCredentialManager {
// ------------------------------ FIELDS ------------------------------
    private static final String DISK_PREFERENCES_NAME = "SE_USER_INFO_PREFERENCES";
    private static final String USER_PHONE_KEY = "USER_PHONE_KEY";
    private static final String USER_NAME_KEY = "USER_NAME_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";
    private static final String STATUS_KEY = "STATUS_KEY";
    private static final String GCM_REG_ID = "GCM_REG_ID";
    private final SharedPreferences diskCache;
    private final SmartExpress box;
    private final Object lock = new Object();
    private volatile boolean loaded = false;
    private SeUser current;

// --------------------------- CONSTRUCTORS ---------------------------
    public SeCredentialManager(SmartExpress box, Context context) {
        this.box = box;
        this.diskCache = context.getSharedPreferences(DISK_PREFERENCES_NAME, Context.MODE_PRIVATE);
        current = load();
        loaded = true;
    }

    private SeUser load() {
        Map<String, ?> userMap = diskCache.getAll();
        if (userMap == null) return null;
        String phone = (String) userMap.get(USER_PHONE_KEY);
        if (phone == null) return null;
        String password = (String) userMap.get(PASSWORD_KEY);
        String name = (String)userMap.get(USER_NAME_KEY);
        String status = (String) userMap.get(STATUS_KEY);
        SeUser user = new SeUser(phone, password, status);
        user.setName(name);
        user.setGcmRegId((String)userMap.get(GCM_REG_ID));

        return user;
    }

// -------------------------- OTHER METHODS --------------------------

    public void clear() {
        synchronized (lock) {
            current = null;
            loaded = false;
            erase();
        }
    }

    private void erase() {
        while (!diskCache.edit().clear().commit()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // swallow
            }
        }
    }



    public SeUser currentUser() {
        if (!loaded) {
            synchronized (lock) {
                if (!loaded) {
                    current = load();
                    loaded = true;
                }
            }
        }
        return current;
    }

    void unbindUser() {
        synchronized (lock){
            current =null;
            loaded=false;
        }
    }

    public void storeUser(SeUser user) {
        synchronized (lock) {
            current = user;
            if (user == null) {
                erase();
            } else {
                persist(user);
            }
            loaded = true;
        }
    }

    private void persist(SeUser user) {
        String phone = user.getPhone();
        String name = user.getName();
        String password = user.getPassword();
        String status = user.getStatus();

        SharedPreferences.Editor edit = diskCache.edit()
                .putString(USER_PHONE_KEY, phone)
                .putString(PASSWORD_KEY, password)
                .putString(STATUS_KEY, status)
                .putString(USER_NAME_KEY, name)
                .putString(GCM_REG_ID, user.getGcmRegId());
        while (!edit.commit()) ;
    }

}
