package ru.smartexpress.courierapp.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.R;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 18.03.15 12:05
 */
public class HistoryOrdersFragment extends AbstractOrderListActivity {
    @Override
    public OrderList getData() {
        return orderDAO.getOrdersByStatus(OrderTaskStatus.DONE.name());
    }

    @Override
    public String getTitle() {
        return "Завершенные";
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        OrderDTO orderDTO = orders.get(position);
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        intent.putExtra(OrderActivity.ORDER_DTO, orderDTO);
        startActivity(intent);
    }

    @Override
    public int getImageResource() {
        return R.drawable.ic_place_selector;
    }
}
