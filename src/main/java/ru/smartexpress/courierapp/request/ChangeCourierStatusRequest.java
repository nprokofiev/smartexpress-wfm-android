package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.common.status.CourierStatus;
import ru.smartexpress.courierapp.CommonConstants;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.04.15 6:53
 */
public class ChangeCourierStatusRequest extends AbstractSpringAndroidRequest<Void> {
    private CourierStatus status;
    public ChangeCourierStatusRequest(CourierStatus status) {
        super(Void.class);
        this.status = status;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/updateCourierStatus";
        getRestTemplate().put(url, status);
        return null;
    }
}
