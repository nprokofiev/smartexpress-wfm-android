package ru.smartexpress.courierapp.core;

import android.util.Log;

import java.util.Locale;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 30.03.16 19:14
 */
public final class Logger {
// ------------------------------ FIELDS ------------------------------

    private static final boolean ENABLED = true;
    private static final String TAG = "SmartExpress";

    // --------------------------- CONSTRUCTORS ---------------------------
    private Logger(){
    }

// -------------------------- STATIC METHODS --------------------------

    public static void warn(String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, String.format(Locale.US, format, args));
        }
    }

    public static void warn(Throwable e,String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, String.format(Locale.US, format, args),e);
        }
    }

    public static void trace(String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, String.format(Locale.US, format, args));
        }
    }

    public static void info(String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, String.format(Locale.US, format, args));
        }
    }

    public static void info(Throwable e,String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, String.format(Locale.US, format, args),e);
        }
    }

    public static void error(String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, String.format(Locale.US, format, args));
        }
    }

    public static void error(Throwable t, String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, String.format(Locale.US, format, args), t);
        }
    }

    public static void debug(String format, Object... args) {
        if (ENABLED && Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, String.format(Locale.US, format, args));
        }
    }
}
