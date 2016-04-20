package ru.smartexpress.courierapp.service.notification;

import android.content.Context;
import android.content.Intent;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.NotificationType;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.activity.MainActivity;
import ru.smartexpress.courierapp.activity.OrderActivity;
import ru.smartexpress.courierapp.order.OrderHelper;

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
    public void handle(MobileMessageDTO messageDTO) {
        OrderDTO orderDTO = messageDTO.getOrder();
        orderDAO.deleteOrder(orderDTO.getId());
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(OrderActivity.ORDER_DTO, orderDTO);
        pingNotify(context.getString(R.string.order_canceled), OrderHelper.getShortDescription(orderDTO), messageDTO.getId().intValue(), intent);

    }
}
