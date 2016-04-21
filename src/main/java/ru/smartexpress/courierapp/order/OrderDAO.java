package ru.smartexpress.courierapp.order;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.smartexpress.common.dto.AddressDTO;
import ru.smartexpress.common.status.OrderTaskStatus;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;

import java.util.List;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 14.03.15 9:27
 */
public class OrderDAO {
    private DBHelper dbHelper;

    private static final String whereOrderId = OrderFields.ID +" = ?";

    public OrderDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    public void saveOrder(OrderDTO orderDTO){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String[] whereArgs = new String[]{orderDTO.getId().toString()};
        Cursor cursor = database.query(OrderFields.TABLE_NAME, null, whereOrderId, whereArgs, null, null, null);
        boolean exist = cursor.moveToFirst();
        ContentValues cv = orderDTO2ContentValues(orderDTO);
        if(exist)
            database.update(OrderFields.TABLE_NAME, cv, whereOrderId, whereArgs);
        else
            database.insert(OrderFields.TABLE_NAME, null, cv);

    }

    public OrderDTO getOrderById(Long orderId){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String[] whereArgs = new String[]{orderId.toString()};
        Cursor cursor = database.query(OrderFields.TABLE_NAME, null, whereOrderId, whereArgs, null, null, null);
        if(!cursor.moveToFirst())
            return null;
        return cursor2orderDTO(cursor);
    }

    public void deleteOrder(Long id){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String[] whereArgs = new String[]{id.toString()};
        database.delete(OrderFields.TABLE_NAME, whereOrderId, whereArgs);
    }

    public void clearAllOrders(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(OrderFields.TABLE_NAME, "", new String[0]);

    }

    public int countCourierSearchOrders(){
         Cursor cursor = getCursorByStatus(OrderTaskStatus.COURIER_SEARCH.name());
        return cursor.getCount();
    }

    public void updateOrderStatus(Long orderId, OrderTaskStatus status){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String[] whereArgs = new String[]{orderId.toString()};
        ContentValues cv = new ContentValues();
        cv.put(OrderFields.STATUS, status.name());
        database.update(OrderFields.TABLE_NAME,cv, whereOrderId, whereArgs);

    }

    public Cursor getCursorByStatus(String status){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String whereStatus = OrderFields.STATUS + " = ?";
        String[] whereArgs = new String[]{status};
        return database.query(OrderFields.TABLE_NAME, null, whereStatus, whereArgs, null, null, null);
    }

    public OrderList getOrdersByStatus(String status){
        OrderList orderDTOs = new OrderList();
        Cursor cursor = getCursorByStatus(status);
        if(cursor.moveToFirst()){
           do{
               orderDTOs.add(cursor2orderDTO(cursor));
           }
           while (cursor.moveToNext());
        }
        return orderDTOs;

    }

    public OrderList getActiveOrders(){
        OrderList orderDTOs = new OrderList();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String whereStatus = OrderFields.STATUS + " IN(?, ?)";
        String[] whereArgs = new String[]{OrderTaskStatus.CONFIRMED.name(), OrderTaskStatus.PICKED_UP.name()};
        Cursor cursor = database.query(OrderFields.TABLE_NAME, null, whereStatus, whereArgs, null, null, null);
        if(cursor.moveToFirst()){
            do{
                orderDTOs.add(cursor2orderDTO(cursor));
            }
            while (cursor.moveToNext());
        }
        return orderDTOs;
    }


    private OrderDTO cursor2orderDTO(Cursor cursor){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(cursor.getLong(cursor.getColumnIndex(OrderFields.ID)));
        orderDTO.setDeadline(cursor.getLong(cursor.getColumnIndex(OrderFields.DEADLINE)));
        orderDTO.setDeadlineFrom(cursor.getLong(cursor.getColumnIndex(OrderFields.DEADLINE_FROM)));

        //Destination Address
        AddressDTO destinationAddress = new AddressDTO();
        destinationAddress.setFirstLine(cursor.getString(cursor.getColumnIndex(OrderFields.DESTINATION_ADDRESS_FIRST_LINE)));
        destinationAddress.setSecondLine(cursor.getString(cursor.getColumnIndex(OrderFields.DESTINATION_ADDRESS_SECOND_LINE)));
        destinationAddress.setLat(cursor.getDouble(cursor.getColumnIndex(OrderFields.DESTINATION_ADDRESS_LAT)));
        destinationAddress.setLng(cursor.getDouble(cursor.getColumnIndex(OrderFields.DESTINATION_ADDRESS_LNG)));
        orderDTO.setDestinationAddress(destinationAddress);

        AddressDTO sourceAddressDTO = new AddressDTO();
        sourceAddressDTO.setFirstLine(cursor.getString(cursor.getColumnIndex(OrderFields.SOURCE_ADDRESS_FIRST_LINE)));
        sourceAddressDTO.setSecondLine(cursor.getString(cursor.getColumnIndex(OrderFields.SOURCE_ADDRESS_SECOND_LINE)));
        sourceAddressDTO.setLat(cursor.getDouble(cursor.getColumnIndex(OrderFields.SOURCE_ADDRESS_LAT)));
        sourceAddressDTO.setLng(cursor.getDouble(cursor.getColumnIndex(OrderFields.SOURCE_ADDRESS_LNG)));
        orderDTO.setSourceAddress(sourceAddressDTO);


        orderDTO.setOrder(cursor.getString(cursor.getColumnIndex(OrderFields.ORDER)));
        orderDTO.setPickUpDeadline(cursor.getLong(cursor.getColumnIndex(OrderFields.PICKUP_DEADLINE)));

        orderDTO.setCustomerName(cursor.getString(cursor.getColumnIndex(OrderFields.CUSTOMER_NAME)));
        orderDTO.setStatus(cursor.getString(cursor.getColumnIndex(OrderFields.STATUS)));
        orderDTO.setCustomerPhone(cursor.getString(cursor.getColumnIndex(OrderFields.CUSTOMER_PHONE)));
        orderDTO.setPartnerName(cursor.getString(cursor.getColumnIndex(OrderFields.PARTNER_NAME)));
        orderDTO.setPartnerPhone(cursor.getString(cursor.getColumnIndex(OrderFields.PARTNER_PHONE)));
        orderDTO.setCost(cursor.getDouble(cursor.getColumnIndex(OrderFields.COST)));
        return orderDTO;
    }

    private ContentValues orderDTO2ContentValues(OrderDTO orderDTO){
        ContentValues cv = new ContentValues();
        cv.put(OrderFields.ID, orderDTO.getId());
        cv.put(OrderFields.DEADLINE, orderDTO.getDeadline());
        cv.put(OrderFields.DEADLINE_FROM, orderDTO.getDeadlineFrom());
        //destination address
        AddressDTO destinationAddress = orderDTO.getDestinationAddress();
        cv.put(OrderFields.DESTINATION_ADDRESS_FIRST_LINE, destinationAddress.getFirstLine());
        cv.put(OrderFields.DESTINATION_ADDRESS_SECOND_LINE, destinationAddress.getSecondLine());
        cv.put(OrderFields.DESTINATION_ADDRESS_LAT, destinationAddress.getLat());
        cv.put(OrderFields.DESTINATION_ADDRESS_LNG, destinationAddress.getLng());

        //source address
        AddressDTO sourceAddress = orderDTO.getSourceAddress();
        cv.put(OrderFields.SOURCE_ADDRESS_FIRST_LINE, sourceAddress.getFirstLine());
        cv.put(OrderFields.SOURCE_ADDRESS_SECOND_LINE, sourceAddress.getSecondLine());
        cv.put(OrderFields.SOURCE_ADDRESS_LAT, sourceAddress.getLat());
        cv.put(OrderFields.SOURCE_ADDRESS_LNG, sourceAddress.getLng());


        cv.put(OrderFields.ORDER, orderDTO.getOrder());
        cv.put(OrderFields.PICKUP_DEADLINE, orderDTO.getPickUpDeadline());
        cv.put(OrderFields.STATUS, orderDTO.getStatus());
        cv.put(OrderFields.CUSTOMER_NAME, orderDTO.getCustomerName());
        cv.put(OrderFields.CUSTOMER_PHONE, orderDTO.getCustomerPhone());
        cv.put(OrderFields.PARTNER_NAME, orderDTO.getPartnerName());
        cv.put(OrderFields.PARTNER_PHONE, orderDTO.getPartnerPhone());
        cv.put(OrderFields.COST, orderDTO.getCost());
        return cv;
    }
}
