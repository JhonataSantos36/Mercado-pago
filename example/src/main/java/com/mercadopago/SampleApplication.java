package com.mercadopago;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initializeLeakCanary();
    }

    private void initializeLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
