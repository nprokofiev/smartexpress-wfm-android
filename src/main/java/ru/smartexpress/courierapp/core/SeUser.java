package ru.smartexpress.courierapp.core;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import org.springframework.web.client.HttpClientErrorException;
import ru.smartexpress.common.dto.*;
import ru.smartexpress.common.dto.Error;
import ru.smartexpress.common.status.CourierStatus;
import ru.smartexpress.courierapp.R;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.helper.SystemHelper;
import ru.smartexpress.courierapp.order.OrderDAO;
import ru.smartexpress.courierapp.order.OrderHelper;
import ru.smartexpress.courierapp.order.UserDAO;
import ru.smartexpress.courierapp.request.LoginRequest;
import ru.smartexpress.courierapp.request.RegistrationRequest;
import ru.smartexpress.courierapp.request.SimpleRequestListener;
import ru.smartexpress.courierapp.service.LocationService;
import ru.smartexpress.courierapp.service.rest.AuthenticationException;
import ru.smartexpress.courierapp.service.rest.GcmException;
import ru.smartexpress.courierapp.service.rest.SeeHttpServerErrorException;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 30.03.16 11:50
 */
public class SeUser {
    private String phone;
    private String name;
    private String password;
    private String status;
    private String gcmRegId;
    private long lastMessageCheck;

    public interface LoginResult{
        SpiceManager getSpiceManager();
        void onLoginSuccess(SeUser user);
        void onLoginFailed(String reason);
    }

    private SeUser(String username) {
        if (TextUtils.isEmpty(username)) throw new IllegalArgumentException("username cannot be empty");
        this.phone = username;
    }

    SeUser(String phone, String password, String status) {
        this.phone = phone;
        this.password = password;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public SeUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void update(UserDTO userDTO){
        phone = userDTO.getPhone();
        name = userDTO.getName();
        status = userDTO.getStatus().toString();
        gcmRegId = userDTO.getGcmRegId();
    }

    public SeUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public SeUser setName(String name){
        this.name = name;
        return this;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public static SeUser withUserName(String phone) {
        SeUser current = current();
        if (current != null && current.phone.equals(phone)) {
            return current;
        }
        return new SeUser(phone);
    }

    public void login(final LoginResult handler){
        LoginRequest loginRequest = new LoginRequest(this);
        handler.getSpiceManager().execute(loginRequest, new RequestListener<UserDTO>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                handler.onLoginFailed(detectRestError(spiceException));
            }

            @Override
            public void onRequestSuccess(UserDTO userDTO) {
                update(userDTO);
               storeUser();
               handler.onLoginSuccess(SeUser.this);
            }
        });
    }

    public void storeUser(){
        SeApplication.smartexpress().store.storeUser(SeUser.this);
    }

    public void logout(){
        SeApplication.smartexpress().store.clear();
    }

    public void goOnline(Context context){
        if(!SystemHelper.isMyServiceRunning(LocationService.class, context)) {
            setStatus(CourierStatus.ONLINE.toString());
            storeUser();
            Intent locationService = new Intent(context, LocationService.class);
            context.startService(locationService);

        }
    }

    public void goOffline(Context context){
        if(SystemHelper.isMyServiceRunning(LocationService.class, context)) {
            Logger.info("senging stop service event");
            Intent locationService = new Intent(context, LocationService.class);
            context.stopService(locationService);
            setStatus(CourierStatus.OFFLINE.toString());
            storeUser();
            OrderDAO orderDAO = new OrderDAO(context);
            orderDAO.clearAllOrders();
            OrderHelper.updateContent(context);
        }
        else {
            Logger.info("service is not running");
        }
    }


    public void register(final LoginResult handler){
        final CourierRegistrationRequest registrationRequest = new CourierRegistrationRequest();
        registrationRequest.setPassword(password);
        registrationRequest.setName(name);
        registrationRequest.setPhone(phone);
        Logger.info("registering user");
        handler.getSpiceManager().execute(new RegistrationRequest(registrationRequest), new RequestListener<CourierRegistrationResult>() {
            @Override
            public void onRequestFailure(SpiceException e) {
                Logger.info("registration failed",e);
                handler.onLoginFailed(detectRestError(e));
            }

            @Override
            public void onRequestSuccess(CourierRegistrationResult result) {
                if(result.errors.size() > 0){
                    for(ErrorMessage error : result.errors) {
                        handler.onLoginFailed(error.getMessage());
                        return;
                    }
                }
                Logger.info("registration ok:" + result.toString());
                SeUser.this.login(handler);
            }
        });
    }

    private String detectRestError(SpiceException spiceException){
        Logger.error(spiceException, "rest error");
        if (spiceException instanceof NetworkException) {
            NetworkException exception = (NetworkException) spiceException;
            Throwable cause = exception.getCause();
            if (cause instanceof SeeHttpServerErrorException) {
                SeeHttpServerErrorException seeHttpServerErrorException = (SeeHttpServerErrorException) exception.getCause();
                ru.smartexpress.common.dto.Error error = seeHttpServerErrorException.getSEEError();
                    return error.getErrorMessage();

            }
            else if(cause instanceof AuthenticationException){
                return SeApplication.smartexpress().context.getString(R.string.invalid_login_or_password);
            }
            else if(cause instanceof GcmException){
                return SeApplication.app().getString(R.string.gcm_problem)+cause.getCause().getMessage();
            }
            else {
                return SeApplication.smartexpress().context.getString(R.string.rest_error);
            }

        }
        else if(spiceException instanceof NoNetworkException){
            return SeApplication.smartexpress().context.getString(R.string.no_network_error);
        }
        else{
            return SeApplication.smartexpress().context.getString(R.string.unknown_error)+" "+spiceException.getMessage();
        }
    }

    public static SeUser current() {
        return SmartExpress.getDefaultChecked().store.currentUser();
    }
}
