package ru.smartexpress.courierapp.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class OrderListActivity extends ListActivity {

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
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        String[] values = new String[] {};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
   //     createOrderListGrid();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        OrderDTO orderDTO = orders.get(position);
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra(OrderActivity.ORDER_DTO, orderDTO);
        startActivity(intent);
     //   String item = (String) getListAdapter().getItem(position);
     //   Toast.makeText(this, item + " выбран", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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



    private void loadData(OrderList orderList){
        orders = orderList;
        List<String> items = new ArrayList<String>();
        for (OrderDTO orderDTO : orders){
           String order = String.format("Заказ №%d из %s, %s на %s до %tR", orderDTO.getId(), orderDTO.getPartnerName(), orderDTO.getSourceAddress(), orderDTO.getDestinationAddress(), new Date(orderDTO.getDeadline()));
           items.add(order);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
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


    public void logout(){
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_PREFS, 0);
        intent.putExtra("username", preferences.getString("username", null));
        intent.putExtra("password", preferences.getString("password", null));
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        startActivity(intent);
        Intent locationService = new Intent(this, LocationService.class);
        stopService(locationService);
        finish();
    }
}
