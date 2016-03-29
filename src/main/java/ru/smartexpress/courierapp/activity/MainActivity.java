package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.app.TabActivity;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 10.03.15 16:30
 */
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.status.CourierStatus;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.helper.AuthHelper;
import ru.smartexpress.courierapp.helper.SystemHelper;
import ru.smartexpress.courierapp.request.ChangeCourierStatusRequest;
import ru.smartexpress.courierapp.request.SimpleRequestListener;
import ru.smartexpress.courierapp.service.JsonSpiceService;
import ru.smartexpress.courierapp.service.LocationService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

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
    ViewPager mViewPager;

    private Menu menu;

    private SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);


    public static final String TAB_INDEX = "tabIndex";
    public static final String UPDATE_CONTENT_ACTION = "updateContentAction";
    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             mAppSectionsPagerAdapter.update();
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!SystemHelper.isMyServiceRunning(LocationService.class, this)) {
            Intent intent = new Intent(this, LocationService.class);
            startService(intent);
        }

        setContentView(R.layout.main_view);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        mAppSectionsPagerAdapter.add(new CourierSearchOrderFragment());
        mAppSectionsPagerAdapter.add(new ActiveOrdersFragment());
        mAppSectionsPagerAdapter.add(new HistoryOrdersFragment());
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        if(updateReceiver!=null){
            IntentFilter intentFilter = new IntentFilter(UPDATE_CONTENT_ACTION);
            registerReceiver(updateReceiver, intentFilter);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tabIndex = intent.getIntExtra(TAB_INDEX, 0);
        Log.i(getClass().getName(), "tabIndex from intent:"+tabIndex);
        mViewPager.setCurrentItem(tabIndex);
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
        if(!AuthHelper.isLoggedIn(this))
            AuthHelper.forceLogout(this);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
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

        public void add(MainActivityFragment fragment){
            fragments.add(fragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(updateReceiver!=null)
            unregisterReceiver(updateReceiver);
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

    public void makeMeOffline(){
        MenuItem item = menu.findItem(R.id.statusMenu);
        item.setIcon(R.drawable.status_offline);
        spiceManager.execute(new ChangeCourierStatusRequest(CourierStatus.OFFLINE.name()), new SimpleRequestListener(this) {
            @Override
            public void onRequestSuccess(Object o) {
                Log.i("Main", "courier offline");
            }
        });
    }

    public void makeMeOnline(){
        MenuItem item = menu.findItem(R.id.statusMenu);
        item.setIcon(R.drawable.status_online);
        spiceManager.execute(new ChangeCourierStatusRequest(CourierStatus.ONLINE.name()), new SimpleRequestListener(this) {
            @Override
            public void onRequestSuccess(Object o) {
                Log.i("Main", "courier online");
            }
        });
    }

    public void logout(){
        makeMeOffline();
        Intent intent = new Intent(this, LoginActivity.class);
        SharedPreferences preferences = getSharedPreferences(LoginActivity.LOGIN_PREFS, 0);
        intent.putExtra("username", preferences.getString("username", null));
        intent.putExtra("password", preferences.getString("password", null));
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        startActivity(intent);
        Intent locationService = new Intent(this, LocationService.class);
        stopService(locationService);
        finish();
    }

}
