package ru.smartexpress.courierapp.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import org.springframework.util.StringUtils;
import ru.smartexpress.common.NotificationField;
import ru.smartexpress.common.NotificationType;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.common.dto.MobileMessageList;
import ru.smartexpress.courierapp.CommonConstants;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.activity.MainActivity;
import ru.smartexpress.courierapp.activity.NewOrderActivity;
import ru.smartexpress.courierapp.helper.SystemHelper;
import ru.smartexpress.courierapp.order.UserDAO;
import ru.smartexpress.courierapp.receiver.GcmBroadcastReceiver;
import ru.smartexpress.courierapp.request.PendingMessagesRequest;
import ru.smartexpress.courierapp.service.notification.NewOrderNotificationHandler;
import ru.smartexpress.courierapp.service.notification.NotificationHandler;
import ru.smartexpress.courierapp.service.notification.OrderAssignedNotificationHandler;
import ru.smartexpress.courierapp.service.notification.OrderUpdatedNotificationHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 24.01.15 18:03
 */
public class GcmIntentService extends IntentService implements RequestListener<MobileMessageList> {
    public GcmIntentService() {
        super(CommonConstants.SENDER_ID);
    }
    public static final String TAG = "GcmIntentService";

    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);


    private List<NotificationHandler> handlers = new ArrayList<NotificationHandler>();

    private Timer timer = new Timer();

    private class BestBeforeTimerTask extends TimerTask {
        private MobileMessageDTO messageDTO;

        private BestBeforeTimerTask(MobileMessageDTO messageDTO) {
            this.messageDTO = messageDTO;
        }

        @Override
        public void run() {
            handleBestBefore(messageDTO);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handlers.add(new NewOrderNotificationHandler(this, spiceManager));
        handlers.add(new OrderAssignedNotificationHandler(this, spiceManager));
        handlers.add(new OrderUpdatedNotificationHandler(this, spiceManager));

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        spiceManager.start(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG, "Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                long offsetId = UserDAO.getLastMessageOffset(this);
                spiceManager.execute(new PendingMessagesRequest(offsetId), this);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.e(TAG, "error obtaining messages", spiceException);
    }

    @Override
    public void onRequestSuccess(MobileMessageList mobileMessageDTOs) {
        Log.i(TAG, "messages loaded:"+mobileMessageDTOs.toString());
        Long offsetId=null;
        for (MobileMessageDTO messageDTO : mobileMessageDTOs) {
            offsetId = messageDTO.getId();
            handleMessage(messageDTO);
        }
        if(offsetId!=null) {
            UserDAO.setLastMessageOffset(this, offsetId);
            SystemHelper.sendUpdateUI(this);
        }
    }





    private void handleBestBefore(MobileMessageDTO messageDTO){
        String type = messageDTO.getType();
        for (NotificationHandler handler : handlers) {
            if (handler.getType().equals(type)) {
                Log.i(TAG, "handling bestBefore for "+messageDTO.toString());
                handler.afterBestBefore(messageDTO);
                return;
            }
        }
    }

    private void handleMessage(MobileMessageDTO messageDTO){
        String type = messageDTO.getType();
        for (NotificationHandler handler : handlers) {
            if (handler.getType().equals(type)) {
                handler.handle(messageDTO);
                Integer bestBeforeInterval = messageDTO.getBestBeforeIntervalSec();
                if(bestBeforeInterval!=null && bestBeforeInterval > 0)
                    timer.schedule(new BestBeforeTimerTask(messageDTO), bestBeforeInterval);
                return;
            }
        }
        Log.e(TAG, "no notification handler found for "+type);
    }
}