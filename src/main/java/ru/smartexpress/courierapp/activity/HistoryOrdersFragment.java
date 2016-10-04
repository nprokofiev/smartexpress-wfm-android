package ru.smartexpress.courierapp.activity;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
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



    public void loadData(){
        setRefreshing(true);
        setListAdapter(null);
        spiceManager.execute(new OrderHistoryRequest(), this);


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
        SeArrayAdapter arrayAdapter = new SeArrayAdapter(getActivity(), R.layout.order_fragment_list_item, orderDTOs);
        setListAdapter(arrayAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

    }



    @Override
    public void forceDataRefresh() {
        loadData();
    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_place_selector;
    }

    @Override
    public void update() {

    }
}
