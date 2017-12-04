package com.projects.melih.helpcity;

import android.app.Application;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

/**
 * Created by melih on 27/11/2017
 */

public class HelpCityApplication extends Application {

    @Override
    public void onCreate() {
        if (BuildConfig.STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        Timber.plant(new Timber.DebugTree());

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}