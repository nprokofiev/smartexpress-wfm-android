package ru.smartexpress.courierapp.order;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 13.03.15 7:46
 */
public interface OrderFields {
    String ID = "id";
    String SOURCE_ADDRESS = "sourceAddress";
    String DESTINATION_ADDRESS = "destinationAddress";
    String ORDER = "orderInfo";
    String DEADLINE = "deadline";
    String PICKUP_DEADLINE = "pickupDeadline";
    String STATUS = "status";
    String TABLE_NAME = "orders";
    String CUSTOMER_NAME = "customerName";
    String PARTNER_NAME = "partnerName";
    String PARTNER_PHONE = "partnerPhone";
    String CUSTOMER_PHONE = "customerPhone";
    String COST = "cost";

}
