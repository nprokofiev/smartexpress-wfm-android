package ru.smartexpress.courierapp.order;

import ru.smartexpress.common.dto.OrderDTO;

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
}
