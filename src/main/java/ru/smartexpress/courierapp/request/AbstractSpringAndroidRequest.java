package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import ru.smartexpress.courierapp.SeApplication;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 31.03.16 19:05
 */
public abstract class AbstractSpringAndroidRequest<RESULT> extends SpringAndroidSpiceRequest<RESULT> {
    protected final String baseUrl = SeApplication.smartexpress().config.getUrlBase();

    public AbstractSpringAndroidRequest(Class<RESULT> clazz) {
        super(clazz);
    }


}
