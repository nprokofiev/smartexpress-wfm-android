package ru.smartexpress.courierapp.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import org.codehaus.jackson.map.ObjectMapper;
import ru.smartexpress.common.dto.Error;
import ru.smartexpress.courierapp.activity.LoginActivity;
import ru.smartexpress.courierapp.helper.AuthHelper;
import ru.smartexpress.courierapp.helper.SystemHelper;
import ru.smartexpress.courierapp.service.rest.SeeHttpServerErrorException;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.03.15 8:27
 */
public abstract class SimpleRequestListener<T> implements RequestListener<T> {
    private Activity context;

    private ObjectMapper objectMapper = new ObjectMapper();

    protected SimpleRequestListener(Activity context) {
        this.context = context;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Error error = processError(spiceException);
        context.setProgressBarIndeterminateVisibility(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Произошла ошибка.").setMessage(error.getErrorMessage())
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

    protected Error processError(SpiceException spiceException){
        Error error = new Error();
        if (spiceException instanceof NetworkException) {
            NetworkException exception = (NetworkException) spiceException;
            if (exception.getCause() instanceof SeeHttpServerErrorException) {
                SeeHttpServerErrorException seeHttpServerErrorException = (SeeHttpServerErrorException) exception.getCause();
                error = seeHttpServerErrorException.getSEEError();
                int seeErrorCode = error.getErrorCode();
                if(seeErrorCode==301){
                    AuthHelper.forceLogout(context);
                }

            }

            }
            return error;
    }


}
