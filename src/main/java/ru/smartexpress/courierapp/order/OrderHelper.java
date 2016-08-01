package ru.smartexpress.courierapp.order;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import ru.smartexpress.common.dto.AddressDTO;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.activity.MainActivity;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 14.03.15 9:12
 */
public class OrderHelper {

    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("0.00");

    public static String getShortDescription(OrderDTO orderDTO){
        return String.format("№%d из %s, %s на %s до %tR", orderDTO.getId(), orderDTO.getPartnerName(), orderDTO.getSourceAddress().getFirstLine(), orderDTO.getDestinationAddress().getFirstLine(), new Date(orderDTO.getDeadline()));
    }

    public static String getOrderHeader(OrderDTO orderDTO){
        String time = null;
        Context context = SeApplication.app().getApplicationContext();
        String deadline = getTime(orderDTO.getDeadline(), context);

        long deadlineFrom = orderDTO.getDeadlineFrom();
        if(deadlineFrom == 0){
            time = context.getString(R.string.deadline_time, deadline);
        }
        else {
            time = context.getString(R.string.deadline_interval, getTime(deadlineFrom, context), deadline);
        }

        return   context.getString(R.string.order_header, orderDTO.getId(), orderDTO.getPartnerName(),  time);

    }

    public static String getCurrency(Double currency){
        Context context = SeApplication.app().getApplicationContext();
        return context.getString(R.string.currency_format, CURRENCY_FORMAT.format(currency));
    }

    public static String getFullAddress(AddressDTO addressDTO){
        return addressDTO.getFirstLine()+" "+addressDTO.getSecondLine();
    }

    public static String getNamePhone(String name, String phone){
         return String.format("%s тел. %s ", name, phone);

    }

    public static String getTime(long unixtime, Context context){
        Date date = new Date(unixtime);
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(context);
        return dateFormat.format(date);
    }



    public static void updateContent(Context context){
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        Intent contentUpdate = new Intent();
        contentUpdate.setAction(MainActivity.UPDATE_CONTENT_ACTION);
        broadcastManager.sendBroadcast(contentUpdate);
    }
}
