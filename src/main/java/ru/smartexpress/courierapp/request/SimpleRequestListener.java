package ru.smartexpress.courierapp.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 04.03.15 8:27
 */
public abstract class SimpleRequestListener<T> implements RequestListener<T> {
    private Activity context;

    protected SimpleRequestListener(Activity context) {
        this.context = context;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        context.setProgressBarIndeterminateVisibility(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Произошла ошибка.")
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


}
