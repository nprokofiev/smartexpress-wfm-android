package ru.smartexpress.courierapp.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import ru.smartexpress.common.dto.CourierLocation;
import ru.smartexpress.common.dto.OrderDTO;
import ru.smartexpress.common.dto.OrderList;
import ru.smartexpress.common.status.CourierStatus;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.activity.MainActivity;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.order.OrderDAO;
import ru.smartexpress.courierapp.order.OrderHelper;
import ru.smartexpress.courierapp.request.ChangeCourierStatusRequest;
import ru.smartexpress.courierapp.request.ConfirmedOrdersRequest;
import ru.smartexpress.courierapp.request.LocationChangedRequest;
import ru.smartexpress.courierapp.service.rest.AuthenticationException;
import ru.smartexpress.courierapp.service.rest.GcmException;
import ru.smartexpress.courierapp.service.rest.SeeHttpServerErrorException;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 11.01.15 1:29
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    public static final int NOTIFICATION_ID=1;
    public static final long LOCATION_UPDATE_INTERVAL = 30000;
    public static final long FASTEST_UPDATE_INTERVAL = 30000;
    private static volatile boolean isRunning;

    public static final String LAST_SUCCESSFUL_LOCATION_UPDATE = "last_successful_location_update";

    private final IBinder mIbinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public LocationService getInstance(){
            return LocationService.this;
        }
    }


    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

    @Override
    public IBinder onBind(Intent intent) {
        return mIbinder;
    }


    @Override
    public void onCreate() {
        spiceManager.start(this);
        super.onCreate();
        SeUser user = SeUser.current();
        if(user ==null){
            throw new RuntimeException("Can't start service without user logged in!!!");
        }

        startNotification();
    //    changeUserStatus(CourierStatus.ONLINE, null);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        syncActiveOrders();
        Logger.info("starting service");
        Logger.info("Location service started");
        Logger.info("Location service creating connection");
        isRunning = true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        Logger.info("locationService connected to google. Starting updates");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.info("connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Logger.info("connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long lastSuccessLocationUpdate = sharedPreferences.getLong(LAST_SUCCESSFUL_LOCATION_UPDATE, 0);
        long updateDiff = System.currentTimeMillis() - lastSuccessLocationUpdate;
        //if time interval > 2 hours syncing up
        if(updateDiff > 2*60*60*1000){
            syncActiveOrders();
        }
        if (location == null)
            return;
        Logger.info( "got location lat:" + location.getLatitude() + " lot:" + location.getLongitude());
        LocationChangedRequest request = new LocationChangedRequest(new CourierLocation(location.getLatitude(), location.getLongitude()));
        spiceManager.execute(request, new RequestListener<Object>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                detectRestError(spiceException);
                Logger.info("location update failed", spiceException);
            }

            @Override
            public void onRequestSuccess(Object s) {
                Logger.info("location updated successfully");
                sharedPreferences.edit().putLong(LAST_SUCCESSFUL_LOCATION_UPDATE, System.currentTimeMillis()).commit();
            }
        });

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void syncActiveOrders(){
        Logger.info("syncing orders");
        spiceManager.execute(new ConfirmedOrdersRequest(), new RequestListener<OrderList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                detectRestError(spiceException);
                Logger.error("Failed to sync orders", spiceException);
            }

            @Override
            public void onRequestSuccess(OrderList orderDTOs) {
                OrderDAO orderDAO = new OrderDAO(LocationService.this);
                orderDAO.clearAllOrders();
                for(OrderDTO orderDTO : orderDTOs)
                    orderDAO.saveOrder(orderDTO);

                OrderHelper.updateContent(LocationService.this);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startNotification(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.working))
                .setContentText(getString(R.string.currently_working_for_smartexpress))
                .setSmallIcon(R.drawable.smartexpress_launcher)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void stopNotification(){
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        spiceManager.shouldStop();
        stopNotification();
        isRunning = false;
        super.onDestroy();
    }

    public void changeUserStatus(final CourierStatus status, final RequestListener<Void> requestListener){
        spiceManager.execute(new ChangeCourierStatusRequest(status), new RequestListener<Void>() {
            @Override
            public void onRequestSuccess(Void o) {
                SeUser user = SeUser.current();
                user.setStatus(status.toString());
                user.storeUser();
                if(requestListener!=null)
                    requestListener.onRequestSuccess(o);
                Logger.info("Status chenged ok to "+status);
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Logger.error(spiceException, "failed to change status");
                if(requestListener!=null)
                    requestListener.onRequestFailure(spiceException);
            }
        });
    }

    private void detectRestError(SpiceException spiceException){
        Logger.error(spiceException, "rest error");
        if (spiceException instanceof NetworkException) {
            NetworkException exception = (NetworkException) spiceException;
            Throwable cause = exception.getCause();
            if (cause instanceof SeeHttpServerErrorException) {
                SeeHttpServerErrorException seeHttpServerErrorException = (SeeHttpServerErrorException) exception.getCause();
                ru.smartexpress.common.dto.Error error = seeHttpServerErrorException.getSEEError();
                // error.getErrorMessage();

            }
            else if(cause instanceof HttpClientErrorException){
                HttpStatus statusCode = ((HttpStatusCodeException)cause).getStatusCode();
                if(HttpStatus.UNAUTHORIZED.equals(statusCode)){
                    SeUser.current().logout();
                    OrderHelper.updateContent(this);
                    stopSelf();
                }

            }

            else {
            }

        }
        else if(spiceException instanceof NoNetworkException){
        }
        else{
        }

    }




    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        onLocationChanged(location);
    }



    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
       Logger.info("location updates stopped");
    }

    public static boolean isRunning(){
        return isRunning;
    }
}
