package ru.smartexpress.courierapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.order.OrderDAO;
import ru.smartexpress.courierapp.order.OrderHelper;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOrderListActivity extends ListFragment implements MainActivityFragment {


    protected OrderDAO orderDAO;


    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    protected List<OrderDTO> orders = new ArrayList<OrderDTO>();



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.order_list);
        orderDAO = new OrderDAO(getActivity());
        loadData();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public abstract OrderList getData();

    @Override
    public Fragment getFragment() {
        return this;
    }


    @Override
    public void update() {
        loadData();
    }

    protected void loadData(){
        orders = getData();
        List<String> items = new ArrayList<String>();
        for (OrderDTO orderDTO : orders){
           String order = OrderHelper.getShortDescription(orderDTO);
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
