package ru.smartexpress.courierapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.core.SeUser;
import ru.smartexpress.courierapp.helper.SystemHelper;
import ru.smartexpress.courierapp.service.JsonSpiceService;

/**
 * smartexpress
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 20.12.14 9:30
 */
public class LoginActivity extends FragmentActivity implements SeUser.LoginResult {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private View mProgressView;
    private View mLoginFormView;
    public static final String LOGIN_PREFS = "usersettings";
    protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    private static final String TAG = "LoginActivity";

    public static final Class<? extends Activity> defaultActivity = MainActivity.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  checkAuth();
        setContentView(R.layout.login);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.login_fragment_container, loginFragment)
                .commit();
        SystemHelper.checkPlayServices(this);

    }




    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onLoginSuccess(SeUser user) {

        showProgress(false);
        user.goOnline(this);

        Intent intent = new Intent(LoginActivity.this, defaultActivity);

        LoginActivity.this.setProgressBarIndeterminateVisibility(false);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFailed(String reason) {
        showProgress(false);
        Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
    }

    @Override
    public SpiceManager getSpiceManager() {
        return spiceManager;
    }





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


}
