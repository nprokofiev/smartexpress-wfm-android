package ru.smartexpress.courierapp.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.R;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 18.03.15 11:54
 */
public class ActiveOrdersFragment extends AbstractOrderListActivity {
    @Override
    public OrderList getData() {
        return orderDAO.getActiveOrders();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        OrderDTO orderDTO = orders.get(position);
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        intent.putExtra(OrderActivity.ORDER_ID, orderDTO.getId());
        startActivity(intent);

    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_favorite_selector;
    }

    @Override
    public String getTitle() {
        return "Активные заказы";
    }
}
