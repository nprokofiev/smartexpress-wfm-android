package ru.smartexpress.courierapp.request;

import ru.smartexpress.common.dto.GcmTokenUpdate;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 05.04.16 13:38
 */
public class GcmTokenUpdateRequest extends AbstractSpringAndroidRequest<GcmTokenUpdate> {

    private String token;

    public GcmTokenUpdateRequest(String token) {
        super(GcmTokenUpdate.class);
        this.token = token;
    }

    @Override
    public GcmTokenUpdate loadDataFromNetwork() throws Exception {
        String url = baseUrl+"/courier/updateGcmToken";
        GcmTokenUpdate tokenUpdate = new GcmTokenUpdate();
        tokenUpdate.setGcmToken(token);
        return getRestTemplate().postForObject(url,tokenUpdate,  GcmTokenUpdate.class);


    }
}
