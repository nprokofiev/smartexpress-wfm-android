package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.NotificationField;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.request.AcceptOrderRequest;
import ru.smartexpress.courierapp.request.RejectOrderRequest;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.Date;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 26.01.15 0:52
 */
public class NewOrderActivity extends Activity {
    TextView sourceAddress;
    TextView destinationAddress;
    TextView pickUpDeadline;
    TextView deadline;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    Button accept;
    Button reject;
    SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private Long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list_item);
        sourceAddress = (TextView)findViewById(R.id.sourceAddressOrderListItem);
        destinationAddress = (TextView)findViewById(R.id.destinationAddressOrderListItem);
        pickUpDeadline = (TextView)findViewById(R.id.pickUpDeadlineOrderListItem);
        deadline = (TextView)findViewById(R.id.deadlineOrderListItem);
        accept = (Button)findViewById(R.id.acceptOrderListItem);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAccept();
            }
        });
        reject = (Button)findViewById(R.id.rejectOrderListItem);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReject();
            }
        });
    }

    private void checkAuth(){
        SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_PREFS, 0);
        if(!preferences.contains("username")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
        checkAuth();
        fillText(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillText(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fillText(intent);

    }

    public void fillText(Intent intent){

        String destinationAddressI = decode(intent.getStringExtra(NotificationField.DESTINATION_ADDRESS));
        String sourceAddressI = decode(intent.getStringExtra(NotificationField.SOURCE_ADDRESS));
        String pickUpDeadlineI = intent.getStringExtra(NotificationField.PICK_UP_DEADLINE);
        String deadlineI = intent.getStringExtra(NotificationField.DEADLINE);
        sourceAddress.setText(sourceAddressI);
        destinationAddress.setText(destinationAddressI);
        pickUpDeadline.setText(dateFormat.format(new Date(Long.valueOf(pickUpDeadlineI))));
        deadline.setText(dateFormat.format(new Date(Long.valueOf(deadlineI))));

        orderId = Long.valueOf(getIntent().getStringExtra(NotificationField.ORDER_ID));


        sourceAddress.invalidate();
        destinationAddress.invalidate();
        pickUpDeadline.invalidate();
        deadline.invalidate();

    }

    public static String decode(String in){
        try {
            return URLDecoder.decode(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("NewOrderActivity", "error during decode", e);
            return in;
        }
    }

    private void onAccept(){
       spiceManager.execute(new AcceptOrderRequest(orderId), new RequestListener() {
           @Override
           public void onRequestFailure(SpiceException spiceException) {
                Log.e("NewOrderActivity", "errorHappend", spiceException);
           }

           @Override
           public void onRequestSuccess(Object o) {
                Log.i("NewOrderActivity", "accepted ok");
               finish();
           }
       });
    }

    private void onReject(){
        spiceManager.execute(new RejectOrderRequest(orderId), new RequestListener() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("NewOrderActivity", "error", spiceException);
            }

            @Override
            public void onRequestSuccess(Object o) {
                Log.i("NewOrderActivity", "rejected ok");
                finish();
            }
        });
    }
}
