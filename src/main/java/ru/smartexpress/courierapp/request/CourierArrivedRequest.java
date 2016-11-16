package ru.smartexpress.courierapp.request;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 14.11.16 19:36
 */
public class CourierArrivedRequest extends AbstractSpringAndroidRequest {
    private long orderId;

    public CourierArrivedRequest(long orderId) {
        super(Object.class);
        this.orderId = orderId;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/courierArrivedForPickup?orderId="+orderId;
        getRestTemplate().getForObject(url, Object.class);
        return null;
    }
}
