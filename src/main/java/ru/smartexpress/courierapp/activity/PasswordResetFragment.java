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
import android.widget.Toast;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import ru.smartexpress.common.dto.PasswordResetCodeDTO;
import ru.smartexpress.common.dto.PasswordResetRequestDTO;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.helper.ValidationHelper;
import ru.smartexpress.courierapp.request.PasswordResetCode;
import ru.smartexpress.courierapp.request.RequestPasswordReset;

/**
 * smartexpress-courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 01.11.16 15:59
 */
public class PasswordResetFragment extends Fragment {
    private EditText phoneNumber;
    private EditText confirmationCode;
    private LoginActivity activity;
    private PasswordResetRequestDTO resetRequestDTO;
    private Button resetPasswordButton;
    private Button requestCodeButton;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        phoneNumber = (EditText) getActivity().findViewById(R.id.phoneText);
        confirmationCode = (EditText) getActivity().findViewById(R.id.confirmationCodeEditText);
        activity = (LoginActivity) getActivity();

        requestCodeButton = (Button)getActivity().findViewById(R.id.requestCodeButton);
        requestCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCode();
            }
        });


        resetPasswordButton = (Button) getActivity().findViewById(R.id.resetButton);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        resetPasswordButton.setEnabled(false);
        confirmationCode.setEnabled(false);

    }

    private void requestCode(){
        String phone = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phone) && ! ValidationHelper.isPhoneValid(phone)){
            phoneNumber.setError(getString(R.string.error_phone_not_valid));
            return;
        }
        activity.getSpiceManager().execute(new RequestPasswordReset(phone), new RequestListener<PasswordResetRequestDTO>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                String errorText = SeUser.detectRestError(spiceException);
                Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRequestSuccess(PasswordResetRequestDTO passwordResetRequestDTO) {
               resetRequestDTO = passwordResetRequestDTO;
                resetPasswordButton.setEnabled(true);
                confirmationCode.setEnabled(true);
                requestCodeButton.setEnabled(false);
                phoneNumber.setEnabled(false);
            }
        });
    }

    private void resetPassword(){
        if(resetRequestDTO==null || System.currentTimeMillis() > resetRequestDTO.getTtl().getTime()){
            phoneNumber.setError(getString(R.string.confirmation_code_must_be_requested));
            return;
        }
        String code = confirmationCode.getText().toString();
        if (TextUtils.isEmpty(code)){
            confirmationCode.setError(getString(R.string.must_not_be_empty));
            return;
        }
        PasswordResetCodeDTO passwordResetCodeDTO = new PasswordResetCodeDTO();
        passwordResetCodeDTO.setCode(code);
        passwordResetCodeDTO.setId(resetRequestDTO.getId());
        activity.getSpiceManager().execute(new PasswordResetCode(passwordResetCodeDTO), new RequestListener() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                String errorText = SeUser.detectRestError(spiceException);
                Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRequestSuccess(Object o) {
                activity.onBackPressed();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_reset_fragment, container, false);
    }
}
