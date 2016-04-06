package ru.smartexpress.courierapp.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Pair;
import android.util.Patterns;
import com.octo.android.robospice.SpiceManager;
import ru.smartexpress.common.status.CourierStatus;
import ru.smartexpress.courierapp.BuildConfig;
import ru.smartexpress.courierapp.SeApplication;
import ru.smartexpress.courierapp.service.JsonSpiceService;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * courier-android
 *
 * @author <a href="mailto:nprokofiev@gmail.com">Nikolay Prokofiev</a>
 * @date 30.03.16 17:40
 */
public class SmartExpress {
    /**
     * Environment
     */
    public static final String ENV = BuildConfig.ENVIRONMENT;
    private static volatile SmartExpress sDefaultClient;
    private static final Object LOCK = new Object();

    private SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
    final SeCredentialManager store;
    final Context context;
    public final Config config;
    private SmartExpress(Context context, Config config) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        this.context = context.getApplicationContext();
        this.config = config;
        this.store = new SeCredentialManager(this, context);
    }

    static SmartExpress getDefaultChecked() {
        if (sDefaultClient == null)
            throw new IllegalStateException("Trying to use implicit client, but no default initialized");
        return sDefaultClient;
    }

    public static void checkServices(){
        SeUser user = SeUser.current();
        if(user!=null){
            String status = user.getStatus();
            if(CourierStatus.ONLINE.toString().equals(status)){
                user.goOnline(SeApplication.app());
            }
        }
    }

    public static Builder builder(Context context){
        return new Builder(context);
    }



    public static final class Config {
        public final ExceptionHandler exceptionHandler;
        public final String password;
        public final int keystoreRes;


        public enum AuthType {
            BASIC_AUTHENTICATION, SESSION_TOKEN
        }


        public final boolean useHttps;


        public final int httpPort;


        public final String apiDomain;


        public final String apiBasepath;


        public final AuthType authenticationType;


        /**
         * GCM SenderId to use for notifications
         */
        public final String senderId;

        public String getUrlBase(){
            StringBuilder sb = new StringBuilder();
            sb.append(useHttps ? "https://":"http://");
            sb.append(apiDomain);
            sb.append(":");
            sb.append(httpPort);
            sb.append(apiBasepath);
            return sb.toString();
        }

        Config(ExceptionHandler exceptionHandler, boolean useHttps, int httpPort,
                String apiDomain, String apiBasepath,
               AuthType authenticationType,
               int keystoreRes,String keystorepass,String senderIds) {
            this.exceptionHandler = exceptionHandler;
            this.useHttps = useHttps;
            this.httpPort = httpPort;
            this.apiDomain = apiDomain;
            this.apiBasepath = apiBasepath;
            this.authenticationType = authenticationType;
            this.keystoreRes=keystoreRes;
            this.password=keystorepass;
            this.senderId = senderIds;
        }
    }

    public static class Builder {
        private final Context mContext;
        private ExceptionHandler mExceptionHandler =  ExceptionHandler.DEFAULT;
        private Config.AuthType mAuthType = Config.AuthType.SESSION_TOKEN;
        private boolean mUseHttps = false;
        private int mPort = 80;
        private String mApiDomain = "10.0.2.2";
        private String mApiBasepath = "/";
        private int mKeyStoreRes = 0;
        private String mKeyStorePass = null;
        private String mSenderIds;


        /**
         * Creates a new builder
         * @param context
        s     */
        public Builder(Context context){
            mContext=context.getApplicationContext();
            mSenderIds = null;
        }



        public Builder setSenderId(String senderId){
            mSenderIds = senderId;
            return this;
        }





        public Builder setExceptionHandler(ExceptionHandler handler){
            mExceptionHandler = handler==null?ExceptionHandler.DEFAULT:handler;
            return this;
        }


        public Builder setAuthentication(Config.AuthType auth){
            mAuthType = auth==null? Config.AuthType.BASIC_AUTHENTICATION:auth;
            return this;
        }

        /**
         * Sets the port this client will connect to
         * @param port
         * @return this builder
         */
        public Builder setPort(int port){
            mPort = port;
            return this;
        }

        /**
         * Sets the api basePath prefix
         * @param basepath
         * @return this builder
         */
        public Builder setApiBasepath(String basepath){
            mApiBasepath = basepath==null?"/":basepath;
            return this;
        }





        /**
         * Sets the host this client will connect to
         * @param domain
         * @return this builder
         */
        @TargetApi(Build.VERSION_CODES.FROYO)
        public Builder setApiDomain(String domain){
            if (domain==null) mApiDomain = "10.0.2.2";
            if(Patterns.IP_ADDRESS.matcher(domain).matches()||
                    Patterns.DOMAIN_NAME.matcher(domain).matches()){
                mApiDomain = domain;
            } else {
                throw new RuntimeException("Invalid host name: "+domain+". Hint: don't specify protocol (eg. http) or path");
            }
            return this;
        }





        public Builder setUseHttps(boolean useHttps){
            this.mUseHttps=useHttps;
            return this;
        }





        private Config buildConfig(){
            return new Config(mExceptionHandler,mUseHttps,
                    mPort
                    ,mApiDomain,
                    mApiBasepath,mAuthType,
                    mKeyStoreRes,
                    mKeyStorePass,
                    mSenderIds);
        }


        public SmartExpress init(){
            if (sDefaultClient==null){
                synchronized (LOCK){
                    if (sDefaultClient==null){
                        SmartExpress box = new SmartExpress(mContext, buildConfig());
                        box.spiceManager.start(mContext);
                        sDefaultClient = box;
                    }
                }
            }
            return sDefaultClient;
        }


    }

}
