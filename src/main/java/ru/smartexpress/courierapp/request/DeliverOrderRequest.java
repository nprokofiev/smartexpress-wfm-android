package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.CommonConstants;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.03.15 7:35
 */
public class DeliverOrderRequest  extends SpringAndroidSpiceRequest {

    private long orderId;

    public DeliverOrderRequest(long orderId) {
        super(Object.class);
        this.orderId = orderId;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        String url = CommonConstants.REST_URL+"/courier/done?orderId="+orderId;
        getRestTemplate().getForObject(url, Object.class);
        return null;

    }


}