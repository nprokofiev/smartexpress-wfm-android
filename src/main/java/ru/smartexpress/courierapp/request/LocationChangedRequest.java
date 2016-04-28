package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.CourierLocation;
import ru.smartexpress.common.dto.HeartBeatDTO;
import ru.smartexpress.courierapp.CommonConstants;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 11.01.15 17:01
 */
public class LocationChangedRequest extends AbstractSpringAndroidRequest<HeartBeatDTO> {
    private CourierLocation location;
    public LocationChangedRequest(CourierLocation location) {
        super(HeartBeatDTO.class);
        this.location = location;

    }

    @Override
    public HeartBeatDTO loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/updateLocation";
        return getRestTemplate().postForObject(url, location, HeartBeatDTO.class);
//        getRestTemplate().postForObject(url, location, String.class);
    }
}
