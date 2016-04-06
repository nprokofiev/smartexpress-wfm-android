package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.helper.AuthHelper;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 18.03.15 11:40
 */
public abstract class UpdatableActivity extends Activity {
    protected BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            onUpdateReceived();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateReceiver, new IntentFilter(MainActivity.UPDATE_CONTENT_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SeUser.current()==null){
            Intent intent = new Intent(this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }

    public abstract void onUpdateReceived();
}
