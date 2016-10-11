package ru.smartexpress.courierapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.data.SimplePagingLoadConfig;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.request.OrderHistoryRequest;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 18.03.15 12:05
 */
public class HistoryOrdersFragment extends AbstractOrderListActivity implements RequestListener<OrderList> {

    private SeArrayAdapter arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayAdapter = new SeArrayAdapter(getActivity(), R.layout.order_fragment_list_item);
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnScrollListener(new InfiniteScrollListener(50) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                SimplePagingLoadConfig loadConfig = new SimplePagingLoadConfig();
                loadConfig.setLimit(50);
                loadConfig.setOffset(totalItemsCount);
                spiceManager.execute(new OrderHistoryRequest(loadConfig), HistoryOrdersFragment.this);

            }
        });
    }

    public void loadData(){
        setRefreshing(true);
        SimplePagingLoadConfig loadConfig = new SimplePagingLoadConfig();
        loadConfig.setLimit(50);
        spiceManager.execute(new OrderHistoryRequest(loadConfig), this);
    }

    @Override
    public OrderList getData() {
        //noop
        return null;
    }

    @Override
    public int getTitle() {
        return R.string.finished_orders;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        setRefreshing(false);
        Log.e("problem", "problem", spiceException);
    }

    @Override
    public void onRequestSuccess(OrderList orderDTOs) {
        setRefreshing(false);
        arrayAdapter.addAll(orderDTOs);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }

    @Override
    public void forceDataRefresh() {
        if(arrayAdapter!=null)
            arrayAdapter.clear();
        loadData();
    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_place_selector;
    }

    @Override
    public void update() {}
}
