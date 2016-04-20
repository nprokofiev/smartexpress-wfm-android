package ru.smartexpress.courierapp.order;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 13.03.15 7:46
 */
public interface OrderFields {
    String ID = "id";
    String SOURCE_ADDRESS_FIRST_LINE = "sourceAddressFirstLine";
    String SOURCE_ADDRESS_SECOND_LINE = "sourceAddressSecondLine";
    String SOURCE_ADDRESS_LAT = "sourceAddressLat";
    String SOURCE_ADDRESS_LNG = "sourceAddressLng";
    String DESTINATION_ADDRESS_FIRST_LINE = "destinationAddressFirstLine";
    String DESTINATION_ADDRESS_SECOND_LINE = "destinationAddressSecondLine";
    String DESTINATION_ADDRESS_LAT = "destinationAddressLat";
    String DESTINATION_ADDRESS_LNG = "destinationAddressLng";
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
