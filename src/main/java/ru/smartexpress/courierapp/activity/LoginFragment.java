package ru.smartexpress.courierapp.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.SeUser;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 01.04.16 12:38
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginFragment extends Fragment {
    private EditText phoneEditText;
    private EditText passwordEditText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.login_fragment, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        phoneEditText = (EditText) getView().findViewById(R.id.userPhoneLoginForm);
        passwordEditText = (EditText) getView().findViewById(R.id.passwordLoginForm);
        Button submit = (Button) getView().findViewById(R.id.submitLoginForm);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });
        Button register = (Button) getView().findViewById(R.id.registerLoginForm);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrationFragment registrationFragment = new RegistrationFragment();
                LoginActivity loginActivity = (LoginActivity)getActivity();
                loginActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_fragment_container, registrationFragment)
                        .addToBackStack(null)
                        .commit();


            }
        });
    }

    private void attemptLogin() {
        LoginActivity activity = (LoginActivity)getActivity();
        if (activity.getSpiceManager().getPendingRequestCount() > 0) {
            return;
        }

        // Reset errors.
        phoneEditText.setError(null);
        passwordEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.must_not_be_empty));
            focusView = passwordEditText;
            cancel = true;
        }



        // Check for a valid phone.
        if (TextUtils.isEmpty(email)) {
            phoneEditText.setError(getString(R.string.must_not_be_empty));
            focusView = phoneEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            activity.showProgress(true);
            SeUser.withUserName(email)
                    .setPassword(password)
                    .login(activity);

        }
    }
}
