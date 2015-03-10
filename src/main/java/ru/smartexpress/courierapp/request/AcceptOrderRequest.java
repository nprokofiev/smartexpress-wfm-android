package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.CommonConstants;

import java.net.URI;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 26.01.15 3:41
 */
public class AcceptOrderRequest extends SpringAndroidSpiceRequest<OrderDTO> {

    private long orderId;

    public AcceptOrderRequest(long orderId) {
        super(OrderDTO.class);
        this.orderId = orderId;
    }

    @Override
    public OrderDTO loadDataFromNetwork() throws Exception {
        String url = CommonConstants.REST_URL+"/courier/acceptOrder?orderId="+orderId;
        return getRestTemplate().postForObject(url,null,  OrderDTO.class);


    }


}
