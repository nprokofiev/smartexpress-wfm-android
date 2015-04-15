package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.courierapp.CommonConstants;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.04.15 6:53
 */
public class ChangeCourierStatusRequest extends SpringAndroidSpiceRequest {
    private String status;
    public ChangeCourierStatusRequest(String status) {
        super(Object.class);
        this.status = status;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        String url = CommonConstants.REST_URL+"/courier/updateCourierStatus?status="+status;
        getRestTemplate().getForObject(url, Object.class);
        return null;
    }
}
