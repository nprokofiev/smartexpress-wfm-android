package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.order.OrderDAO;
import ru.smartexpress.courierapp.order.OrderHelper;
import ru.smartexpress.courierapp.request.AcceptOrderRequest;
import ru.smartexpress.courierapp.request.RejectOrderRequest;
import ru.smartexpress.courierapp.request.SimpleRequestListener;
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
public class NewOrderActivity extends UpdatableActivity {
    TextView sourceAddress;
    TextView destinationAddress;
    TextView pickUpDeadline;
    TextView deadline;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    Button accept;
    Button reject;
    SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    OrderDAO orderDAO = new OrderDAO(this);
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

    }



    @Override
    protected void onStart() {
        super.onStart();
        if(!spiceManager.isStarted()){
            spiceManager.start(this);
        }
        checkAuth();
        fillText(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillText(getIntent());
    }




    @Override
    public void onUpdateReceived() {
        OrderDTO orderDTO =  orderDAO.getOrderById(orderId);
        //destroying activity if it's out of best before
        if(orderDTO==null)
            finish();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fillText(intent);

    }

    public void fillText(Intent intent){

        orderId = intent.getLongExtra(OrderActivity.ORDER_ID, 0L);

        if(orderId.equals(0L))
            finish();

        OrderDTO orderDTO = orderDAO.getOrderById(orderId);





        sourceAddress.setText(OrderHelper.getFullAddress(orderDTO.getSourceAddress()));
        destinationAddress.setText(OrderHelper.getFullAddress(orderDTO.getDestinationAddress()));
            pickUpDeadline.setText(dateFormat.format(new Date(Long.valueOf(orderDTO.getPickUpDeadline()))));
            deadline.setText(dateFormat.format(new Date(Long.valueOf(orderDTO.getDeadline()))));



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
        setProgressBarIndeterminateVisibility(true);
        setButtonEnabled(false);
        spiceManager.execute(new AcceptOrderRequest(orderId), new RequestListener<OrderDTO>() {
           @Override
           public void onRequestFailure(SpiceException spiceException) {
               setButtonEnabled(true);
               NewOrderActivity.this.setProgressBarIndeterminateVisibility(false);
               AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);
               builder.setMessage("Не удалось принять заказ, произошла ошибка.")
                       .setCancelable(false)
                       .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                           }
                       });
               AlertDialog alertDialog =  builder.create();
               alertDialog.show();
                Log.e("NewOrderActivity", "errorHappend", spiceException);
           }

           @Override
           public void onRequestSuccess(OrderDTO o) {
               setButtonEnabled(true);
               o.setStatus(OrderTaskStatus.CONFIRMED.name());
               orderDAO.saveOrder(o);
               NewOrderActivity.this.setProgressBarIndeterminateVisibility(false);
               Log.i("NewOrderActivity", "accepted ok");
               Intent intent = new Intent(NewOrderActivity.this, MainActivity.class);
               intent.putExtra(MainActivity.TAB_INDEX, 1);
               startActivity(intent);

               finish();
           }
       });
    }

    private void onReject(){
        setButtonEnabled(false);
        spiceManager.execute(new RejectOrderRequest(orderId), new SimpleRequestListener(this) {


            @Override
            public void onRequestSuccess(Object o) {
                setButtonEnabled(true);
                Log.i("NewOrderActivity", "rejected ok");
                orderDAO.deleteOrder(orderId);
                finish();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                setButtonEnabled(true);
                super.onRequestFailure(spiceException);
            }
        });
    }

    private void setButtonEnabled(boolean enabled){
        reject.setEnabled(enabled);
        accept.setEnabled(enabled);
    }
}
