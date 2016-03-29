package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.dto.UserDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.helper.AuthHelper;
import ru.smartexpress.courierapp.request.LoginRequest;
import ru.smartexpress.courierapp.service.JsonSpiceService;

/**
 * smartexpress
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 20.12.14 9:30
 */
public class LoginActivity extends Activity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private EditText phoneEditText;
    private EditText passwordEditText;
    public static final String LOGIN_PREFS = "usersettings";
    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private static final String TAG = "LoginActivity";

    public static final Class<? extends Activity> defaultActivity = MainActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAuth();
        setContentView(R.layout.login);
        phoneEditText = (EditText) findViewById(R.id.userPhoneLoginForm);
        passwordEditText = (EditText) findViewById(R.id.passwordLoginForm);
        Button submit = (Button) findViewById(R.id.submitLoginForm);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onLogin(phoneEditText.getText().toString(), passwordEditText.getText().toString());
                Log.i(TAG, "clicked");
            }
        });
        Button register = (Button) findViewById(R.id.registerLoginForm);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent runRegisterActivity = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(runRegisterActivity);
            }
        });

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Google play services не обнаружены. Устройство не поддерживается.")
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog =  builder.create();
                alertDialog.show();
            }
            return false;
        }
        return true;
    }

    private void checkAuth(){
        if(AuthHelper.isLoggedIn(this)){
            Intent intent = new Intent(LoginActivity.this, defaultActivity);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onStart() {
        spiceManager.start(this);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        if(username!=null)
            phoneEditText.setText(username);
        if(password!=null)
            passwordEditText.setText(password);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    private void onLogin(final String phone, final String password){
        if(!checkPlayServices()){
           return;
        }
        setProgressBarIndeterminateVisibility(true);
        final LoginRequest loginRequest = new LoginRequest(phone, password, this);
        Log.i(TAG, "executong login");
        spiceManager.execute(loginRequest, new RequestListener<UserDTO>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Log.i(TAG, "login failed:"+e.getMessage());
                LoginActivity.this.setProgressBarIndeterminateVisibility(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Ошибка авторизации")
                        .setMessage("Ошибка:"+e.getCause().toString())
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            }

            @Override
            public void onRequestSuccess(UserDTO courier) {
                Log.i(TAG, "login ok:"+courier.toString());
                AuthHelper.login(phone, password, LoginActivity.this);
                Intent intent = new Intent(LoginActivity.this, defaultActivity);
                LoginActivity.this.setProgressBarIndeterminateVisibility(false);
                startActivity(intent);
                finish();
            }
        });

    }


}
