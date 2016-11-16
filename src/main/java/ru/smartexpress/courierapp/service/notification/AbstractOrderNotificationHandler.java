package ru.smartexpress.courierapp.service.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.order.OrderDAO;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 15.03.15 8:11
 */
public abstract class AbstractOrderNotificationHandler implements NotificationHandler  {


    protected Context context;

    protected OrderDAO orderDAO;

    protected SpiceManager spiceManager;

    protected NotificationManager mNotificationManager;

    public static final int NOTIFICATION_ID = 112;

    public AbstractOrderNotificationHandler(Context context, SpiceManager spiceManager) {
        this.context = context;
        orderDAO = new OrderDAO(context);
        this.spiceManager = spiceManager;
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    protected void pingNotify(String title, String text, int notificationId, Intent intent){

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_favorite_selected_24dp)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setLights(Color.RED, 3000, 3000)
                        .setSound(uri)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))
                        .setContentText(text);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
