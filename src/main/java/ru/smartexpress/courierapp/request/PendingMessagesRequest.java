package ru.smartexpress.courierapp.request;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.common.dto.MobileMessageList;
import ru.smartexpress.courierapp.CommonConstants;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.03.15 21:05
 */
public class PendingMessagesRequest extends SpringAndroidSpiceRequest<MobileMessageList> {
    private long offsetId;
    public PendingMessagesRequest(long offsetId) {
        super(MobileMessageList.class);
        this.offsetId = offsetId;
        setRetryPolicy(new DefaultRetryPolicy(10, 20000, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public MobileMessageList loadDataFromNetwork() throws Exception {
        String url = CommonConstants.REST_URL+"/courier/getPendingMessages?offsetId="+offsetId;
        return getRestTemplate().getForObject(url, MobileMessageList.class);
    }
}
