package ru.smartexpress.courierapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.service.LocationService;

public class OrderListActivity extends Activity {


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
   //     createOrderListGrid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*    public void createOrderListGrid(){
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(R.id.orderList);
        root.addView(progressBar);
    }*/


    public void logout(){
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
