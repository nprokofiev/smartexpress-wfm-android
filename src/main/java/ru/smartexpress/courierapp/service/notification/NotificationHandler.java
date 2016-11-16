package ru.smartexpress.courierapp.service.notification;

import android.content.Context;
import android.os.Bundle;
import ru.smartexpress.common.NotificationType;
import ru.smartexpress.common.dto.MobileMessageDTO;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 14.03.15 7:21
 */
public interface NotificationHandler {
    String getType();
    void handle(MobileMessageDTO message);
}
