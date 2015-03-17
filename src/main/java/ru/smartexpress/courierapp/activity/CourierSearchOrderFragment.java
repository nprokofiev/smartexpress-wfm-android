package ru.smartexpress.courierapp.activity;

import ru.smartexpress.common.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.order.OrderDAO;

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
    public String getTitle() {
        return "Предлагаемые";
    }


}
