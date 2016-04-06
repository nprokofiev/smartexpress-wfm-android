package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.courierapp.CommonConstants;

import java.util.List;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 05.03.15 8:48
 */
public class ConfirmedOrdersRequest extends AbstractSpringAndroidRequest<OrderList> {

    public ConfirmedOrdersRequest() {
        super(OrderList.class);
    }

    @Override
    public OrderList loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/getConfirmedOrders";
        return getRestTemplate().getForObject(url, OrderList.class);
    }
}
