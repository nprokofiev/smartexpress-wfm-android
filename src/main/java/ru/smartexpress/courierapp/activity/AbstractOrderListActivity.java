package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.order.OrderDAO;
import ru.smartexpress.courierapp.order.OrderHelper;
import ru.smartexpress.courierapp.request.ConfirmedOrdersRequest;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOrderListActivity extends ListFragment implements SeActivityFragment {

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);


    protected OrderDAO orderDAO;


    DateFormat dateFormat = DateFormat.getDateTimeInstance();
    protected List<OrderDTO> orders = new ArrayList<OrderDTO>();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
  //      update();
    }

    @Override
    public void onStart() {

        spiceManager.start(this.getContext());
        super.onStart();
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected void setRefreshing(boolean refreshing){
        Activity activity = getActivity();
        if(activity==null)
            return;
       SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshMain);
        if(swipeRefreshLayout!=null)
            swipeRefreshLayout.setRefreshing(refreshing);
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

    @Override
    public void forceDataRefresh() {
        setRefreshing(true);
        spiceManager.execute(new ConfirmedOrdersRequest(), new RequestListener<OrderList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                setRefreshing(false);
                Logger.error("Failed to sync orders", spiceException);
            }

            @Override
            public void onRequestSuccess(OrderList orderDTOs) {
                setRefreshing(false);
                OrderDAO orderDAO = new OrderDAO(getContext());
                orderDAO.clearAllOrders();
                for(OrderDTO orderDTO : orderDTOs)
                    orderDAO.saveOrder(orderDTO);

                OrderHelper.updateContent(getContext());
            }
        });
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
