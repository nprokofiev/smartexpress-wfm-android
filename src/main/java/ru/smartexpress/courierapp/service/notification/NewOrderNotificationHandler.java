package ru.smartexpress.courierapp.service.notification;

import android.content.Context;
import android.content.Intent;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.NotificationType;
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.activity.MainActivity;
import ru.smartexpress.courierapp.helper.SystemHelper;
import ru.smartexpress.courierapp.order.OrderHelper;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 14.03.15 7:22
 */
public class NewOrderNotificationHandler extends AbstractOrderNotificationHandler {
    public static final int NEW_ORDER_NOTIFICATION_ID = 56473;


    public NewOrderNotificationHandler(Context context, SpiceManager spiceManager) {
        super(context, spiceManager);
    }

    @Override
    public String getType() {
        return NotificationType.NEW_ORDER;
    }

    @Override
    public void afterBestBefore(MobileMessageDTO message) {
        Long orderId = message.getOrder().getId();
        OrderDTO orderDTO = orderDAO.getOrderById(orderId);
        if(OrderTaskStatus.COURIER_SEARCH.equals(orderDTO.getStatus())){
            orderDAO.deleteOrder(orderId);
            SystemHelper.sendUpdateUI(context);
        }
        checkNotification(orderDTO);
    }

    @Override
    public void handle(MobileMessageDTO messageDTO) {
        OrderDTO orderDTO = messageDTO.getOrder();
        orderDAO.saveOrder(orderDTO);
        checkNotification(orderDTO);
    }

    private void checkNotification(OrderDTO orderDTO){
        int newOrderCount = orderDAO.countCourierSearchOrders();
        if(newOrderCount==0){
           mNotificationManager.cancel(NEW_ORDER_NOTIFICATION_ID);
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.TAB_INDEX, 0);


        String title = "Вам предлагают заказ";
        String address;
        if(newOrderCount == 1)
            address = OrderHelper.getShortDescription(orderDTO);
        else
            address = String.format("Поступило %d новых заказа", newOrderCount);
        pingNotify(title, address, NEW_ORDER_NOTIFICATION_ID, intent);
    }


}
