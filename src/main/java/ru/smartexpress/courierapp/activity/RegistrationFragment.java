package ru.smartexpress.courierapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.helper.ValidationHelper;

import java.util.regex.Pattern;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.01.15 17:29
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {

    private EditText mName;
    private EditText mPhone;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mActivationCode;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = (EditText)getActivity().findViewById(R.id.nameEditText);
    //    name2field.put("name", name);
        mPhone = (EditText)getActivity().findViewById(R.id.phoneEditText);
    //    name2field.put("phone", phone);
        mPassword = (EditText)getActivity().findViewById(R.id.passwordEditText);
    //    name2field.put("password", password);
        mConfirmPassword = (EditText)getActivity().findViewById(R.id.confirmPasswordEditText);

        mActivationCode = (EditText)getActivity().findViewById(R.id.activationCodeEditText);

        Button register = (Button)getActivity().findViewById(R.id.registerButton);
        register.setOnClickListener(this);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_fragment, container, false);
    }

    private void attemptRegister() {
        LoginActivity activity = (LoginActivity)getActivity();
        if (activity.getSpiceManager().getPendingRequestCount() > 0){
            return;
        }

        mName.setError(null);
        mPhone.setError(null);
        mPassword.setError(null);

        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        String name = mName.getText().toString();
        String activationCode = mActivationCode.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(password) || !isPasswordValid(password)){
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView=mPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)){
            mConfirmPassword.setError(getString(R.string.error_password_does_not_match));
            focusView=mConfirmPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.must_not_be_empty));
            focusView = mName;
            cancel = true;
        }

        if (TextUtils.isEmpty(activationCode)) {
            mActivationCode.setError(getString(R.string.must_not_be_empty));
            focusView = mActivationCode;
            cancel = true;
        }

        if (TextUtils.isEmpty(phone) && ! ValidationHelper.isPhoneValid(phone)){
            mPhone.setError(getString(R.string.error_phone_not_valid));
            focusView = mPhone;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            activity.showProgress(true);
            SeUser.withUserName(phone)
                    .setPhone(phone)
                    .setPassword(password)
                    .setName(name)
                    .register(activity, activationCode);
        }
    }



    private boolean isPasswordValid(String password) {
        return Pattern.matches("[a-zA-Z0-9]{6,250}", password);
    }



    @Override
    public void onClick(View v) {
        attemptRegister();
    }
}
