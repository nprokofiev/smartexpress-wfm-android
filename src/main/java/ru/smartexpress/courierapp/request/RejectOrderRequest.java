package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.OrderRejectDTO;
import ru.smartexpress.courierapp.CommonConstants;

import java.net.URI;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 26.01.15 4:12
 */
public class RejectOrderRequest extends AbstractSpringAndroidRequest{
    private long orderId;
    private String reason;
    public RejectOrderRequest(long orderId, String reason) {
        super(Object.class);
        this.orderId = orderId;
        this.reason = reason;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/rejectOrder";
        OrderRejectDTO rejectDTO = new OrderRejectDTO();
        rejectDTO.setOrderId(orderId);
        rejectDTO.setReason(reason);
        getRestTemplate().put(url, rejectDTO);
        return null;

    }
}
