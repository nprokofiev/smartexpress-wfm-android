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
    String DEADLINE_FROM = "deadlineFrom";
    String PICKUP_DEADLINE = "pickupDeadline";
    String STATUS = "status";
    String TABLE_NAME = "orders";
    String CUSTOMER_NAME = "customerName";
    String PARTNER_NAME = "partnerName";
    String PARTNER_PHONE = "partnerPhone";
    String CUSTOMER_PHONE = "customerPhone";
    String PROFIT = "profit";
    String CHANGE_FOR = "changeFor";
    String EXTERNAL_HUMAN_ID = "externalHumanId";
    String PAYMENT_DEBS = "paymentDeps";
    String BUYOUT_SUM = "buyoutSum";

     String TABLE_CREATE =
            "CREATE TABLE "+OrderFields.TABLE_NAME+" (" +
                    OrderFields.ID + " INTEGER PRIMARY KEY, " +
                    OrderFields.SOURCE_ADDRESS_FIRST_LINE + " TEXT, " +
                    OrderFields.SOURCE_ADDRESS_SECOND_LINE + " TEXT, " +
                    OrderFields.SOURCE_ADDRESS_LAT + " REAL, " +
                    OrderFields.SOURCE_ADDRESS_LNG + " REAL, " +

                    OrderFields.DESTINATION_ADDRESS_FIRST_LINE + " TEXT, " +
                    OrderFields.DESTINATION_ADDRESS_SECOND_LINE + " TEXT, " +
                    OrderFields.DESTINATION_ADDRESS_LAT + " REAL, " +
                    OrderFields.DESTINATION_ADDRESS_LNG + " REAL, " +

                    OrderFields.ORDER + " TEXT, " +
                    OrderFields.DEADLINE + " INTEGER, " +
                    OrderFields.DEADLINE_FROM + " INTEGER, " +
                    OrderFields.STATUS + " TEXT, " +
                    OrderFields.PARTNER_NAME + " TEXT, " +
                    OrderFields.CUSTOMER_NAME + " TEXT, " +
                    OrderFields.PARTNER_PHONE + " TEXT, " +
                    OrderFields.CUSTOMER_PHONE + " TEXT, " +
                    OrderFields.PROFIT + " REAL, " +
                    OrderFields.PICKUP_DEADLINE + " INTEGER, " +

                    OrderFields.CHANGE_FOR + " TEXT, " +
                    OrderFields.EXTERNAL_HUMAN_ID + " TEXT, " +
                    OrderFields.PAYMENT_DEBS + " TEXT, " +
                    OrderFields.BUYOUT_SUM + " REAL " +
                    ");";

}
