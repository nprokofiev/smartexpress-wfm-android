package ru.smartexpress.courierapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.status.CourierStatus;
import ru.smartexpress.courierapp.BuildConfig;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.core.SmartExpress;
import ru.smartexpress.courierapp.service.JsonSpiceService;
import ru.smartexpress.courierapp.service.LocationService;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 10.03.15 16:30
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */




    private LocationService locationService;

    private Menu menu;


    private SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean mIsBound;

    public static final String TAB_INDEX = "tabIndex";
    public static final String UPDATE_CONTENT_ACTION = "updateContentAction";
    public static final int GPS_PERMISSION_REQUEST = 0;
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SeUser.current()==null){
                startLoginScreen();
                return;
            }

            String action = intent.getAction();
            Logger.info("got intent"+action);
            mAppSectionsPagerAdapter.update();


        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(updateReceiver!=null){
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
            IntentFilter intentFilter = new IntentFilter(UPDATE_CONTENT_ACTION);
            broadcastManager.registerReceiver(updateReceiver, intentFilter);
        }

        if (SeUser.current()==null){
            startLoginScreen();
            return;
        }




        setContentView(R.layout.main_view);
        // Setup the viewPager

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(this);
        mAppSectionsPagerAdapter.add(new CourierSearchOrderFragment());
        mAppSectionsPagerAdapter.add(new ActiveOrdersFragment());
        mAppSectionsPagerAdapter.add(new HistoryOrdersFragment());
        mAppSectionsPagerAdapter.init();


        doBindService();

    }



    private void startLoginScreen(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tabIndex = intent.getIntExtra(TAB_INDEX, 0);
        Log.i(getClass().getName(), "tabIndex from intent:"+tabIndex);

        if(tabIndex==2)
            mAppSectionsPagerAdapter.getFragments().get(2).forceDataRefresh();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(!spiceManager.isStarted()){
            spiceManager.start(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ensureServices();
       /* if(!AuthHelper.isLoggedIn(this))
            AuthHelper.forceLogout(this);*/
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        if(updateReceiver!=null ) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
            broadcastManager.unregisterReceiver(updateReceiver);
        }
        if(spiceManager.isStarted())
            spiceManager.shouldStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        this.menu = menu;
        checkUserStatus();
        //inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case R.id.about:
                showAboutDialog();
                return true;
            case R.id.finance:
                openFinance();
                return true;
            case R.id.statusFree:
                makeMeOnline();
                return true;
            case R.id.statusBusy:
                makeMeOffline();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openFinance(){
        Intent intent = new Intent();
        intent.setClass(this, AccountActivity.class);
        startActivity(intent);
    }

    private void checkUserStatus(){
        SeUser user = SeUser.current();
        Logger.info("current user status is"+user.getStatus());
        if(menu==null)
            return;
        final MenuItem item = menu.findItem(R.id.statusMenu);
        if(CourierStatus.ONLINE.toString().equals(user.getStatus())){
            Logger.info("setting ui status to online");
            item.setIcon(R.drawable.status_online);
        }
        else {
            Logger.info("setting ui status to offline");
            item.setIcon(R.drawable.status_offline);
        }

    }

    public void makeMeOffline(){
        SeUser.current().goOffline(this);
        menu.findItem(R.id.statusMenu).setIcon(R.drawable.status_offline);
    }

    public void makeMeOnline(){
        SeUser.current().goOnline(this);
        menu.findItem(R.id.statusMenu).setIcon(R.drawable.status_online);

    }

    public void logout(){
        SeUser.current().goOffline(this);
        SeUser.current().logout();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void ensureServices(){
        checkUserStatus();
        checkGps();
        SmartExpress.checkServices();
    }

    private void checkGps() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, GPS_PERMISSION_REQUEST);
            return;
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
        case GPS_PERMISSION_REQUEST: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SmartExpress.checkServices();

            } else {

                logout();
            }
            return;
        }


    }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.gps_disabled_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        logout();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            Logger.info("Connected to location service");
            locationService = ((LocationService.LocalBinder)iBinder).getInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            Logger.info("Disconnected from location service");
            locationService = null;
        }
    };

    private void doBindService()
    {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this,
                LocationService.class), mConnection, 0);
        mIsBound = true;
    }

    private void showAboutDialog(){
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.error("failed to get packet", e);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.about_see,  pInfo.versionName, BuildConfig.ENVIRONMENT));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Отпускает диалоговое окно
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doUnbindService()
    {
        if (mIsBound)
        {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

}
