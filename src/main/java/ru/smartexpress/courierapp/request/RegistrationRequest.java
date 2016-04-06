package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.dto.CourierRegistrationRequest;
import ru.smartexpress.common.dto.CourierRegistrationResult;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.core.Logger;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.01.15 17:38
 */
public class RegistrationRequest extends AbstractSpringAndroidRequest<CourierRegistrationResult> {
    private CourierRegistrationRequest courierRegistrationRequest;

    public RegistrationRequest(CourierRegistrationRequest courierRegistrationRequest) {
        super(CourierRegistrationResult.class);
        this.courierRegistrationRequest = courierRegistrationRequest;

    }



    @Override
    public CourierRegistrationResult loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/registerNewCourier";
        Logger.info("executing registration on:"+url);
        return getRestTemplate().postForObject(url, courierRegistrationRequest, CourierRegistrationResult.class);
    }
}
