package ru.smartexpress.courierapp.order;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.activity.MainActivity;

import java.util.Date;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 14.03.15 9:12
 */
public class OrderHelper {
    public static String getShortDescription(OrderDTO orderDTO){
        return String.format("Заказ №%d из %s, %s на %s до %tR", orderDTO.getId(), orderDTO.getPartnerName(), orderDTO.getSourceAddress(), orderDTO.getDestinationAddress(), new Date(orderDTO.getDeadline()));
    }

    public static String getNamePhone(String name, String phone){
         return String.format("%s тел. %s ", name, phone);

    }

    public static void updateContent(Context context){
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        Intent contentUpdate = new Intent();
        contentUpdate.setAction(MainActivity.UPDATE_CONTENT_ACTION);
        broadcastManager.sendBroadcast(contentUpdate);
    }
}
