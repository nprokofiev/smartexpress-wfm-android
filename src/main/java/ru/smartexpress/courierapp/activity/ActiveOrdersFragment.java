package ru.smartexpress.courierapp.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;

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
        intent.putExtra(OrderActivity.ORDER_DTO, orderDTO);
        startActivity(intent);

    }



    @Override
    public String getTitle() {
        return "Активные заказы";
    }
}
