package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import ru.smartexpress.common.dto.AddressDTO;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.helper.AuthHelper;
import ru.smartexpress.courierapp.order.OrderHelper;

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

    protected OrderDTO orderDTO;

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




    protected Spanned getUrlNavigationForAddress(AddressDTO addressDTO){
        Double lat = addressDTO.getLat();
        Double lng = addressDTO.getLng();
        Uri mapsUri = null;
        if(lat!=null && !lat.equals(0.0)){
            mapsUri = Uri.parse(String.format("http://maps.google.com/maps?q=%s,%s", lat, lng));
        }
        else {
            mapsUri = Uri.parse(String.format("http://maps.google.com/maps?q=%s", addressDTO.getFirstLine()));
        }

        return Html.fromHtml("<a href=\""+mapsUri+"\">"+ OrderHelper.getFullAddress(addressDTO)+"</a>");
    }

    public abstract void onUpdateReceived();
}
