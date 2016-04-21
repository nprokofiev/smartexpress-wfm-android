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
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.R;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public abstract OrderList getData();

    @Override
    public Fragment getFragment() {
        return this;
    }


    @Override
    public void update() {
        if(orderDAO==null)
            return;
        loadData();
    }

    protected void loadData(){
        orders = getData();

        SeArrayAdapter arrayAdapter = new SeArrayAdapter(getActivity(), R.layout.order_fragment_list_item, orders);
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
