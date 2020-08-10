package com.freshhome.CommonUtil;

/**
 * Created  on 08-10-16.
 */

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;
        try {
            FirebaseApp.initializeApp(this);
//            CommonMethods.setLocale("en",this);
        } catch (Exception e) {
        }
    }


    public static synchronized MyApplication getInstance() {

        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }


}