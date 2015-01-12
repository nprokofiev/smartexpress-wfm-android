package ru.smartexpress.courierapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.dto.CourierRegistrationRequest;
import ru.smartexpress.common.dto.CourierRegistrationResult;
import ru.smartexpress.common.dto.ErrorMessage;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.request.RegistrationRequest;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.util.HashMap;
import java.util.Map;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.01.15 17:29
 */
public class RegistrationActivity extends Activity implements View.OnClickListener {
    private EditText name;
    private EditText phone;
    private EditText password;
    private EditText confirmPassword;
    private Map<String, EditText> name2field = new HashMap<String, EditText>();
    private static final String TAG = "registrationActivity";
    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.registration);
        name = (EditText)findViewById(R.id.nameEditText);
        name2field.put("name", name);
        phone = (EditText)findViewById(R.id.phoneEditText);
        name2field.put("phone", phone);
        password = (EditText)findViewById(R.id.passwordEditText);
        name2field.put("password", password);
        confirmPassword = (EditText)findViewById(R.id.confirmPasswordEditText);
        Button register = (Button)findViewById(R.id.registerButton);
        register.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        setProgressBarIndeterminateVisibility(true);
        String passwd = password.getText().toString();
        String confirmPasswd = confirmPassword.getText().toString();
        if(!passwd.equals(confirmPasswd)){
            confirmPassword.setError("Пароли не совпадают");
            return;
        }
        final CourierRegistrationRequest registrationRequest = new CourierRegistrationRequest();
        registrationRequest.setPassword(passwd);
        registrationRequest.setName(name.getText().toString());
        registrationRequest.setPhone(phone.getText().toString());
        Log.i(TAG, "registering user");
        spiceManager.execute(new RegistrationRequest(registrationRequest), new RequestListener<CourierRegistrationResult>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Log.i(TAG, "registration failed");
               setProgressBarIndeterminateVisibility(false);

            }

            @Override
            public void onRequestSuccess(CourierRegistrationResult result) {
                setProgressBarIndeterminateVisibility(false);
                if(result.errors.size() > 0){
                    for(ErrorMessage message : result.errors){
                        Log.i(TAG, message.toString());
                        EditText field = name2field.get(message.getField());
                        if(field!=null)
                            field.setError(message.getMessage());

                    }
                    return;
                }

                Log.i(TAG, "registration ok:" + result.toString());
                Intent runLoginActivity = new Intent(RegistrationActivity.this, LoginActivity.class);
                runLoginActivity.putExtra("username", registrationRequest.getPhone());
                runLoginActivity.putExtra("password", registrationRequest.getPassword());
                startActivity(runLoginActivity);
            }
        });
    }
}
