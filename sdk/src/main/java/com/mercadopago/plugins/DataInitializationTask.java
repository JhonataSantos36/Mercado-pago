package com.mercadopago.plugins;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class DataInitializationTask {

    private DataInitializationCallbacks listener;
    private final Map<String, Object> data = new HashMap<>();
    private Thread taskThread;

    public DataInitializationTask(@NonNull final Map<String, Object> data) {
        this.data.putAll(data);
    }

    public void execute(final DataInitializationCallbacks callbacks) {
        taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onLoadData(data);
                    if (!taskThread.isInterrupted()) {
                        callbacks.onDataInitialized(data);
                    }
                } catch (final Exception e) {
                    //Do nothing
                }
            }
        });
        taskThread.start();
    }

    public void cancel() {
        if (taskThread != null && taskThread.isAlive() && !taskThread.isInterrupted()) {
            Log.d("loaddata", "cancel load...");
            taskThread.interrupt();
        }
    }

    public abstract void onLoadData(@NonNull final Map<String, Object> data);

    public interface DataInitializationCallbacks {
        void onDataInitialized(@NonNull final Map<String, Object> data);
    }
}