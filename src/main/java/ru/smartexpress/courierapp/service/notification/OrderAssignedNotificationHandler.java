package ru.smartexpress.courierapp.service.notification;

import android.content.Context;
import android.content.Intent;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.NotificationType;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.activity.OrderActivity;
import ru.smartexpress.courierapp.order.OrderHelper;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.03.15 22:14
 */
public class OrderAssignedNotificationHandler extends AbstractOrderNotificationHandler {

    public OrderAssignedNotificationHandler(Context context, SpiceManager spiceManager) {
        super(context, spiceManager);
    }

    @Override
    public String getType() {
        return NotificationType.ASSIGNED_ORDER;
    }

    @Override
    public void handle(MobileMessageDTO extras) {
        OrderDTO orderDTO = extras.getOrder();
        orderDAO.saveOrder(orderDTO);
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(OrderActivity.ORDER_ID, orderDTO.getId());
        pingNotify(context.getString(R.string.order_assigned_to_you), OrderHelper.getShortDescription(orderDTO), extras.getId().intValue(), intent);
    }
}
