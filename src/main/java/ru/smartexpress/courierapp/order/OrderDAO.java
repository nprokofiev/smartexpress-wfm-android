package ru.smartexpress.courierapp.order;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.smartexpress.common.OrderTaskStatus;
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

    public int countCourierSearchOrders(){
         Cursor cursor = getCursorByStatus(OrderTaskStatus.COURIER_SEARCH.name());
        return cursor.getCount();
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

    private OrderDTO cursor2orderDTO(Cursor cursor){
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(cursor.getLong(cursor.getColumnIndex(OrderFields.ID)));
        orderDTO.setDeadline(cursor.getLong(cursor.getColumnIndex(OrderFields.DEADLINE)));
        orderDTO.setDestinationAddress(cursor.getString(cursor.getColumnIndex(OrderFields.DESTINATION_ADDRESS)));
        orderDTO.setOrder(cursor.getString(cursor.getColumnIndex(OrderFields.ORDER)));
        orderDTO.setPickUpDeadline(cursor.getLong(cursor.getColumnIndex(OrderFields.PICKUP_DEADLINE)));
        orderDTO.setSourceAddress(cursor.getString(cursor.getColumnIndex(OrderFields.SOURCE_ADDRESS)));
        orderDTO.setStatus(cursor.getString(cursor.getColumnIndex(OrderFields.STATUS)));
        return orderDTO;
    }

    private ContentValues orderDTO2ContentValues(OrderDTO orderDTO){
        ContentValues cv = new ContentValues();
        cv.put(OrderFields.ID, orderDTO.getId());
        cv.put(OrderFields.DEADLINE, orderDTO.getDeadline());
        cv.put(OrderFields.DESTINATION_ADDRESS, orderDTO.getDestinationAddress());
        cv.put(OrderFields.ORDER, orderDTO.getOrder());
        cv.put(OrderFields.PICKUP_DEADLINE, orderDTO.getPickUpDeadline());
        cv.put(OrderFields.SOURCE_ADDRESS, orderDTO.getSourceAddress());
        cv.put(OrderFields.STATUS, orderDTO.getStatus());
        return cv;
    }
}
