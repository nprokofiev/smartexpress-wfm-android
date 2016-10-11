package ru.smartexpress.courierapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import ru.smartexpress.common.dto.AddressDTO;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.core.Logger;
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

    public static final int CALL_PERMISSION_CODE = 1;

    protected BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            onUpdateReceived();
        }
    };

    protected OrderDTO orderDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (SeUser.current() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }


    protected void dialNumber(String number) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.CALL_PHONE};
            ActivityCompat.requestPermissions(this, permissions, CALL_PERMISSION_CODE);
            return;
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        String formattedNumber = null;
        try {
            phoneNumber = phoneNumberUtil.parse(number, "RU");
            formattedNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        } catch (Exception e) {
            Logger.info("error parsing phone"+number);
            return;
        }
        if(formattedNumber==null)
            formattedNumber = number;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(formattedNumber));
        startActivity(intent);
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
