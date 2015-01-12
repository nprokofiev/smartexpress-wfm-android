package ru.smartexpress.courierapp.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.dto.CourierLocation;
import ru.smartexpress.courierapp.request.LocationChangedRequest;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 11.01.15 1:29
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG ="SeLocationService";
    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i(TAG, "creating connection");
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        Log.i(TAG, "starting updates");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null)
            return;
        Log.i(TAG, "got location lat:"+location.getLatitude()+" lot:"+location.getLongitude());
        LocationChangedRequest request = new LocationChangedRequest(new CourierLocation(location.getLatitude(), location.getLongitude()));
        spiceManager.execute(request, new RequestListener<Object>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.i(TAG, "location update failed", spiceException);
            }

            @Override
            public void onRequestSuccess(Object s) {
                Log.i(TAG, "location updated successfully");
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        spiceManager.start(this);
        Log.i(TAG, "starting service");

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        spiceManager.shouldStop();
        super.onDestroy();
    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        onLocationChanged(location);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.i(TAG, "location updates stopped");
    }
}
