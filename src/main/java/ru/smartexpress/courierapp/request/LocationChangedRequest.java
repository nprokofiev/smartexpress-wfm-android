package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.CourierLocation;
import ru.smartexpress.courierapp.CommonConstants;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 11.01.15 17:01
 */
public class LocationChangedRequest extends SpringAndroidSpiceRequest {
    private CourierLocation location;
    public LocationChangedRequest(CourierLocation location) {
        super(Object.class);
        this.location = location;

    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        String url = CommonConstants.REST_URL+"/courier/updateLocation";
        getRestTemplate().put(url, location);
        return null;
//        getRestTemplate().postForObject(url, location, String.class);
    }
}
