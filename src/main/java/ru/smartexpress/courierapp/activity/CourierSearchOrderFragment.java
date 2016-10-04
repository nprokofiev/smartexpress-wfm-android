package ru.smartexpress.courierapp.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.R;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 16.03.15 7:44
 */
public class CourierSearchOrderFragment extends AbstractOrderListActivity {



    @Override
    public OrderList getData() {
        return orderDAO.getOrdersByStatus(OrderTaskStatus.COURIER_SEARCH.name());

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        OrderDTO orderDTO = orders.get(position);
        Intent intent = new Intent(getActivity(), NewOrderActivity.class);
        intent.putExtra(OrderActivity.ORDER_ID, orderDTO.getId());
        startActivity(intent);

    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_add_shopping_cart_selector;
    }

    @Override
    public int getTitle() {
        return R.string.courier_search_orders;
    }


}
