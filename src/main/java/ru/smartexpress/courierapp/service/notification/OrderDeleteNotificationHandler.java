package ru.smartexpress.courierapp.service.notification;

import android.content.Context;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.NotificationType;
import ru.smartexpress.common.dto.MobileMessageDTO;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.03.15 22:26
 */
public class OrderDeleteNotificationHandler extends AbstractOrderNotificationHandler {
    public OrderDeleteNotificationHandler(Context context, SpiceManager spiceManager) {
        super(context, spiceManager);
    }

    @Override
    public String getType() {
        return NotificationType.DELETE_ORDER;
    }

    @Override
    public void handle(MobileMessageDTO extras) {

    }
}
