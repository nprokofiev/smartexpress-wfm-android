package ru.smartexpress.courierapp.service.notification;

import android.content.Context;
import android.util.Log;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.dto.MobileMessageDTO;
import ru.smartexpress.common.dto.MobileMessageList;
import ru.smartexpress.courierapp.helper.SystemHelper;
import ru.smartexpress.courierapp.order.UserDAO;
import ru.smartexpress.courierapp.request.PendingMessagesRequest;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 29.03.15 17:18
 */
public class NotificationProcessor implements RequestListener<MobileMessageList> {
    public static final String TAG = NotificationProcessor.class.getName();
    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private Context context;

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

    public NotificationProcessor(Context context) {
        this.context = context;
        handlers.add(new NewOrderNotificationHandler(context, spiceManager));
        handlers.add(new OrderAssignedNotificationHandler(context, spiceManager));
        handlers.add(new OrderUpdatedNotificationHandler(context, spiceManager));
        spiceManager.start(context);
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
            UserDAO.setLastMessageOffset(context, offsetId);
            SystemHelper.sendUpdateUI(context);
        }
    }

    public void processPendingMessages(){
        long offsetId = UserDAO.getLastMessageOffset(context);
        spiceManager.execute(new PendingMessagesRequest(offsetId), this);

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
