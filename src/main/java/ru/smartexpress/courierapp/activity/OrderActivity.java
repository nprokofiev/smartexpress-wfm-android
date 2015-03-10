package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.request.DeliverOrderRequest;
import ru.smartexpress.courierapp.request.PickUpOrderRequest;
import ru.smartexpress.courierapp.request.SimpleRequestListener;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.text.DateFormat;
import java.util.Date;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 22.02.15 14:41
 */
public class OrderActivity extends Activity implements View.OnClickListener {

    private Button accept;
    private TextView sourceAddress;
    private TextView destinationAddress;
    private TextView pickUpDeadline;
    private TextView deadline;
    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private boolean isPickedUp;
    private OrderDTO orderDTO;
    SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    public static final String ORDER_DTO= "orderDTO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_tracking);
        accept = (Button)findViewById(R.id.changeOrderStatus);
        accept.setOnClickListener(this);
        sourceAddress = (TextView)findViewById(R.id.sourceAddress);
        destinationAddress = (TextView)findViewById(R.id.destinationAddress);
        pickUpDeadline = (TextView)findViewById(R.id.pickUpDeadline);
        deadline = (TextView)findViewById(R.id.deadline);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        orderDTO = (OrderDTO)intent.getSerializableExtra(ORDER_DTO);
        sourceAddress.setText(orderDTO.getSourceAddress());
        destinationAddress.setText(orderDTO.getDestinationAddress());
        pickUpDeadline.setText(dateFormat.format(new Date(orderDTO.getPickUpDeadline())));
        deadline.setText(dateFormat.format(new Date(orderDTO.getDeadline())));

    }

    @Override
    public void onClick(View v) {

        SpringAndroidSpiceRequest request;
        setProgressBarIndeterminateVisibility(true);
        if(!isPickedUp){
            request = new PickUpOrderRequest(orderDTO.getId());
            spiceManager.execute(request, new SimpleRequestListener(this) {
                @Override
                public void onRequestSuccess(Object o) {
                    accept.setText("Я доставил заказ");
                    isPickedUp = true;
                }
            });

        }
        else {
            request = new DeliverOrderRequest(orderDTO.getId());
            spiceManager.execute(request, new SimpleRequestListener(this) {
                @Override
                public void onRequestSuccess(Object o) {
                   OrderActivity.this.finish();
                }
            });
        }

    }
}




