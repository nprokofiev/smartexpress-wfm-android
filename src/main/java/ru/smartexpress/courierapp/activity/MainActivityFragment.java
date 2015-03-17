package ru.smartexpress.courierapp.activity;

import android.support.v4.app.Fragment;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 13.03.15 7:09
 */
public interface MainActivityFragment {
    String getTitle();
    Fragment getFragment();
    void update();
}
