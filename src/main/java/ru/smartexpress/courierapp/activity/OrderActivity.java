package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.PaymentDeptDTO;
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.activity.finance.AccountFragment;
import ru.smartexpress.courierapp.order.OrderDAO;
import ru.smartexpress.courierapp.order.OrderHelper;
import ru.smartexpress.courierapp.request.DeliverOrderRequest;
import ru.smartexpress.courierapp.request.PickUpOrderRequest;
import ru.smartexpress.courierapp.request.SimpleRequestListener;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 22.02.15 14:41
 */
public class OrderActivity extends UpdatableActivity implements View.OnClickListener {

    private Button accept;
    private TextView sourceAddress;
    private TextView destinationAddress;
    private TextView pickUpDeadline;
    private TextView deadline;
    private TextView partnerName;
    private Button customerName;
    private TextView orderContents;
    private TextView profit;
    private TextView changeFor;
    private TextView externalHumanId;

    private RelativeLayout footerLayout;
    private ListView customerCollections;
    private TextView buyoutSum;

    private PaymentDebtAdapter paymentDebtAdapter;

    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    public static final NumberFormat moneyFormat = AccountFragment.moneyFormat;
    SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    public static final String ORDER_DTO= "orderDTO";
    public static final String ORDER_ID = "orderId";
    private OrderDAO orderDAO;


    class PaymentDebtAdapter extends ArrayAdapter<PaymentDeptDTO> {

        public PaymentDebtAdapter(Context context, @LayoutRes int resource, @NonNull List<PaymentDeptDTO> objects) {
            super(context, resource, 0, objects);
        }

        public PaymentDebtAdapter(Context context, @LayoutRes int resource) {
            super(context, resource);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.transaction_layout, null);
            }

            PaymentDeptDTO p = getItem(position);

            if (p != null) {
                TextView bookedDate = (TextView) v.findViewById(R.id.transactionBookedDate);
                TextView sum = (TextView) v.findViewById(R.id.transactionSum);
                TextView details = (TextView) v.findViewById(R.id.transactionDetails);


                sum.setText(moneyFormat.format(p.getPaymentSum()));

                bookedDate.setText(p.getLocalizedPaymentType());

                details.setText(p.getPaymentDetails());

            }

            return v;


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_tracking);
        accept = (Button)findViewById(R.id.changeOrderStatus);
        accept.setOnClickListener(this);

        sourceAddress = (TextView)findViewById(R.id.sourceAddress);
        sourceAddress.setMovementMethod(LinkMovementMethod.getInstance());

        destinationAddress = (TextView)findViewById(R.id.destinationAddress);
        destinationAddress.setMovementMethod(LinkMovementMethod.getInstance());

        pickUpDeadline = (TextView)findViewById(R.id.pickUpDeadline);
        deadline = (TextView)findViewById(R.id.deliveryDeadline);
        partnerName = (TextView)findViewById(R.id.partnerName);
        customerName = (Button)findViewById(R.id.customerName);
        customerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialNumber(orderDTO.getCustomerPhone());
            }
        });
        orderContents = (TextView)findViewById(R.id.orderContents);
        profit = (TextView)findViewById(R.id.orderProfit);
        footerLayout = (RelativeLayout)findViewById(R.id.orderTrackingFooter);
        changeFor = (TextView)findViewById(R.id.changeFor);
        externalHumanId = (TextView)findViewById(R.id.externalHumanId);
        buyoutSum = (TextView)findViewById(R.id.orderBuyoutSum);

        customerCollections = (ListView)findViewById(R.id.customerCollection);

        orderDAO = new OrderDAO(this);
        paymentDebtAdapter = new PaymentDebtAdapter(this, R.layout.transaction_layout);
        customerCollections.setAdapter(paymentDebtAdapter);

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        Long orderId = intent.getLongExtra(ORDER_ID, 0L);

        if(orderId.equals(0L)) {
            finish();
            return;
        }

        orderDTO = orderDAO.getOrderById(orderId);
        if(orderDTO==null) {
            finish();
            return;
        }
        setUIStatus(orderDTO.getStatus());


        if(!spiceManager.isStarted()){
            spiceManager.start(this);
        }
        updateUI();
    }

    private void updateUI(){
        if(orderDTO == null)
            finish();
        sourceAddress.setText(getUrlNavigationForAddress(orderDTO.getSourceAddress()));
        destinationAddress.setText(getUrlNavigationForAddress(orderDTO.getDestinationAddress()));
        pickUpDeadline.setText(dateFormat.format(new Date(orderDTO.getPickUpDeadline())));
        deadline.setText(dateFormat.format(new Date(orderDTO.getDeadline())));
        partnerName.setText(OrderHelper.getNamePhone(orderDTO.getPartnerName(), orderDTO.getPartnerPhone()));

        customerName.setText(OrderHelper.getNamePhone(orderDTO.getCustomerName(), orderDTO.getCustomerPhone()));


        orderContents.setText(orderDTO.getOrder());
        profit.setText(OrderHelper.getCurrency(orderDTO.getProfit()));
        changeFor.setText(orderDTO.getChangeFor());
        externalHumanId.setText(orderDTO.getExternalHumanId());

        List<PaymentDeptDTO> customerDept = Arrays.asList(orderDTO.getCollections());
        paymentDebtAdapter.clear();
        paymentDebtAdapter.addAll(customerDept);

        buyoutSum.setText(moneyFormat.format(orderDTO.getBuyoutSum()));
        setListViewHeightBasedOnChildren(customerCollections);
    }


    @Override
    public void onUpdateReceived() {
        orderDTO = orderDAO.getOrderById(orderDTO.getId());
        if(orderDTO == null)
            finish();
        updateUI();

    }

    private void setUIStatus(String status){
        if(OrderTaskStatus.CONFIRMED.name().equals(status)){
            accept.setText(getString(R.string.i_picked_up_this_order));
            accept.setVisibility(View.VISIBLE);
            footerLayout.setVisibility(View.VISIBLE);
        }
        else if(OrderTaskStatus.PICKED_UP.name().equals(status)){
            accept.setText(getString(R.string.i_delivered_this_order));
            accept.setVisibility(View.VISIBLE);
            footerLayout.setVisibility(View.VISIBLE);

        }
        else{
            accept.setVisibility(View.INVISIBLE);
            footerLayout.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onClick(View v) {

        SpringAndroidSpiceRequest request;
        setProgressBarIndeterminateVisibility(true);
        accept.setEnabled(false);
        if(OrderTaskStatus.CONFIRMED.name().equals(orderDTO.getStatus())){
            request = new PickUpOrderRequest(orderDTO.getId());
            spiceManager.execute(request, new SimpleRequestListener(this) {
                @Override
                public void onRequestSuccess(Object o) {
                    accept.setEnabled(true);
                    orderDTO.setStatus(OrderTaskStatus.PICKED_UP.name());
                    setUIStatus(orderDTO.getStatus());
                    orderDAO.updateOrderStatus(orderDTO.getId(), OrderTaskStatus.PICKED_UP);
                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.TAB_INDEX, 1);
                    startActivity(intent);
                    OrderHelper.updateContent(getBaseContext());
                    Toast.makeText(SeApplication.app(), getString(R.string.updated_ok), Toast.LENGTH_LONG);
                    finish();

                }

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    accept.setEnabled(true);
                    super.onRequestFailure(spiceException);
                }
            });

        }
        else {
            request = new DeliverOrderRequest(orderDTO.getId());
            spiceManager.execute(request, new SimpleRequestListener(this) {
                @Override
                public void onRequestSuccess(Object o) {
                    accept.setEnabled(true);
                    orderDAO.deleteOrder(orderDTO.getId());
                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.TAB_INDEX, 2);
                    startActivity(intent);
                    Toast.makeText(SeApplication.app(), getString(R.string.done_ok), Toast.LENGTH_LONG);
                    OrderHelper.updateContent(getBaseContext());
                    finish();
                }

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    accept.setEnabled(true);
                    super.onRequestFailure(spiceException);
                }
            });
        }

    }
}






