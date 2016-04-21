package ru.smartexpress.courierapp.activity;

import android.annotation.TargetApi;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.status.CourierStatus;
import ru.smartexpress.courierapp.BuildConfig;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.core.SmartExpress;
import ru.smartexpress.courierapp.service.JsonSpiceService;
import ru.smartexpress.courierapp.service.LocationService;

import java.util.ArrayList;
import java.util.List;

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

    private TabLayout mTabLayout;





    private Menu menu;


    private SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

    private LocationService locationService;

    private boolean mIsBound;

    public static final String TAB_INDEX = "tabIndex";
    public static final String UPDATE_CONTENT_ACTION = "updateContentAction";
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
        /*if(true){
            throw new RuntimeException("TEST FAILURE");
        }*/

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
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        mAppSectionsPagerAdapter.add(new CourierSearchOrderFragment());
        mAppSectionsPagerAdapter.add(new ActiveOrdersFragment());
        mAppSectionsPagerAdapter.add(new HistoryOrdersFragment());

        viewPager.setAdapter(mAppSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            Logger.info("iterating over item#"+i);
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(mAppSectionsPagerAdapter.getTabView(i));
        }

        mTabLayout.getTabAt(0).getCustomView().setSelected(true);

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
        int pos = mTabLayout.getSelectedTabPosition();
        mTabLayout.getTabAt(pos).getCustomView().setSelected(false);
        mTabLayout.getTabAt(tabIndex).getCustomView().setSelected(true);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setCurrentItem(tabIndex);

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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        private List<MainActivityFragment> fragments = new ArrayList<MainActivityFragment>();
        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i).getFragment();
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void update(){
            for (MainActivityFragment fragment : fragments)
                fragment.update();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }

        public View getTabView(int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(fragments.get(position).getTitle());
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(fragments.get(position).getImageResource());
            return view;
        }

        public void add(MainActivityFragment fragment){


            fragments.add(fragment);
        }
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
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
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
