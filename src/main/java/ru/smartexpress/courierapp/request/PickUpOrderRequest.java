package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.courierapp.CommonConstants;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.03.15 7:27
 */
public class PickUpOrderRequest extends AbstractSpringAndroidRequest {

    private long orderId;

    public PickUpOrderRequest(long orderId) {
        super(Object.class);
        this.orderId = orderId;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/pickUp?orderId="+orderId;
        getRestTemplate().getForObject(url, Object.class);
        return null;
    }
}
