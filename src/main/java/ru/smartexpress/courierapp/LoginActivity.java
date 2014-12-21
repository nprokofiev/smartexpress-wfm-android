package ru.smartexpress.courierapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * smartexpress
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 20.12.14 9:30
 */
public class LoginActivity extends Activity {
    private EditText phoneEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        phoneEditText = (EditText) findViewById(R.id.userPhoneLoginForm);
        passwordEditText = (EditText) findViewById(R.id.passwordLoginForm);
        Button submit = (Button) findViewById(R.id.submitLoginForm);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogin(phoneEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

    }

    private void onLogin(String phone, String password){

    }
}
