package ru.smartexpress.courierapp.request;

import ru.smartexpress.common.data.SimplePagingLoadConfig;
import ru.smartexpress.common.dto.OrderList;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 19.09.16 17:39
 */
public class OrderHistoryRequest extends AbstractSpringAndroidRequest<OrderList> {

    public OrderHistoryRequest() {
        super(OrderList.class);
    }

    @Override
    public OrderList loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/getOrderHistory";
        return getRestTemplate().postForObject(url, new SimplePagingLoadConfig(), OrderList.class);
    }
}
