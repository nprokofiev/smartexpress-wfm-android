package ru.smartexpress.courierapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.request.ConfirmedOrdersRequest;
import ru.smartexpress.courierapp.request.SimpleRequestListener;
import ru.smartexpress.courierapp.service.JsonSpiceService;
import ru.smartexpress.courierapp.service.LocationService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderListActivity extends ListFragment implements MainActivityFragment {

    private SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private List<OrderDTO> orders = new ArrayList<OrderDTO>();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.order_list);

        String[] values = new String[] {};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
   //     createOrderListGrid();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        OrderDTO orderDTO = orders.get(position);
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        intent.putExtra(OrderActivity.ORDER_DTO, orderDTO);
        startActivity(intent);
     //   String item = (String) getListAdapter().getItem(position);
     //   Toast.makeText(this, item + " выбран", Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onStart() {
        if(!spiceManager.isStarted())
        spiceManager.start(this);
        super.onStart();
        spiceManager.execute(new ConfirmedOrdersRequest(), new SimpleRequestListener<OrderList>(this) {
            @Override
            public void onRequestSuccess(OrderList orderDTOs) {
                    loadData(orderDTOs);
            }
        });
    }



    protected void loadData(OrderList orderList){
        orders = orderList;
        List<String> items = new ArrayList<String>();
        for (OrderDTO orderDTO : orders){
           String order = String.format("Заказ №%d из %s, %s на %s до %tR", orderDTO.getId(), orderDTO.getPartnerName(), orderDTO.getSourceAddress(), orderDTO.getDestinationAddress(), new Date(orderDTO.getDeadline()));
           items.add(order);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        setListAdapter(arrayAdapter);
    }

    /*    public void createOrderListGrid(){
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(R.id.orderList);
        root.addView(progressBar);
    }*/



}
