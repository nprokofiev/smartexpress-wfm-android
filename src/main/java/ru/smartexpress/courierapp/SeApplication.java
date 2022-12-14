package ru.smartexpress.courierapp;

import android.app.Application;
import android.content.Context;
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import ru.smartexpress.courierapp.core.Logger;
import ru.smartexpress.courierapp.core.SmartExpress;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 30.03.16 19:49
 */

@ReportsCrashes(formUri = "http://"+CommonConstants.PROD_HOST+"/se/rest/courier/mobileErrorReport/",
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        mode = ReportingInteractionMode.TOAST,

        resToastText = R.string.application_failed)
public class SeApplication extends Application {
    private SmartExpress smartExpress;
    private static SeApplication self;

    public interface Env {
        String DEV = "dev";
        String PROD = "prod";
        String UAT = "uat";
    }

    private Thread.UncaughtExceptionHandler androidDefaultUEH;

                 private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
                 public void uncaughtException(Thread thread, Throwable ex) {
                     Logger.error(ex, "uncaught fail");
                         // log it & phone home.
                         androidDefaultUEH.uncaughtException(thread, ex);
                     }
             };




    @Override
    public void onCreate() {
        super.onCreate();
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        if(Env.DEV.equals(BuildConfig.ENVIRONMENT)){
            initDev();
        }
        else if(Env.PROD.equals(BuildConfig.ENVIRONMENT)){
            initProd();
        }
        else if(Env.UAT.equals(BuildConfig.ENVIRONMENT)){
            initUat();
        }
        else {
            throw new IllegalStateException("Bad environment variable!");
        }

        self = this;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    public static final SeApplication app(){
        return self;
    }

    public static final SmartExpress smartexpress(){
        return self.smartExpress;
    }

    void initDev(){
        smartExpress = SmartExpress.builder(this)
                .setApiDomain(CommonConstants.DEV_HOST)
                .setPort(CommonConstants.DEV_PORT)
                .setSenderId(CommonConstants.DEV_SENDER_ID)
                .setApiBasepath(CommonConstants.DEV_PATH)
                .init();
    }

    void initProd(){
        smartExpress = SmartExpress.builder(this)
                .setApiDomain(CommonConstants.PROD_HOST)
                .setPort(CommonConstants.PROD_PORT)
                .setSenderId(CommonConstants.PROD_SENDER_ID)
                .setApiBasepath(CommonConstants.PROD_PATH)
                .init();
    }

    void initUat(){
        smartExpress = SmartExpress.builder(this)
                .setApiDomain(CommonConstants.UAT_HOST)
                .setPort(CommonConstants.PROD_PORT)
                .setSenderId(CommonConstants.UAT_SENDER_ID)
                .setApiBasepath(CommonConstants.PROD_PATH)
                .init();
    }

}
