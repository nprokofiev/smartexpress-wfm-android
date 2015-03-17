package ru.smartexpress.courierapp.service.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.NotificationType;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.activity.MainActivity;
import ru.smartexpress.courierapp.activity.OrderActivity;
import ru.smartexpress.courierapp.order.OrderHelper;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.03.15 8:03
 */
public class OrderUpdatedNotificationHandler extends AbstractOrderNotificationHandler {

    public OrderUpdatedNotificationHandler(Context context, SpiceManager spiceManager) {
        super(context, spiceManager);
    }

    @Override
    public String getType() {
        return NotificationType.UPDATED_ORDER;
    }

    @Override
    public void handle(MobileMessageDTO messageDTO) {
        OrderDTO orderDTO = messageDTO.getOrder();
        orderDAO.saveOrder(orderDTO);
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(OrderActivity.ORDER_DTO, orderDTO);
        pingNotify("Заказ обновлен оператором", OrderHelper.getShortDescription(orderDTO), messageDTO.getId().intValue(), intent);
    }


}
