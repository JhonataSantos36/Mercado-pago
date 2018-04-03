package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.core.CheckoutStore;

import java.util.Map;

public abstract class DataInitializationTask {

    public static final String KEY_INIT_SUCCESS = "init_success";

    private final Map<String, Object> data;
    private Thread taskThread;

    public DataInitializationTask(@NonNull final Map<String, Object> defaultData) {
        data = CheckoutStore.getInstance().getData();
        data.clear();
        data.putAll(defaultData);
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
                    callbacks.onFailure(e, data);
                }
            }
        });
        taskThread.start();
    }

    public void cancel() {
        if (taskThread != null && taskThread.isAlive() && !taskThread.isInterrupted()) {
            taskThread.interrupt();
        }
    }

    public abstract void onLoadData(@NonNull final Map<String, Object> data);

    public interface DataInitializationCallbacks {
        void onDataInitialized(@NonNull final Map<String, Object> data);
        void onFailure(@NonNull final Exception e, @NonNull final Map<String, Object> data);
    }
}