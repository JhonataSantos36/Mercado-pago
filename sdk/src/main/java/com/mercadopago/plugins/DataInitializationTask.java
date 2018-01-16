package com.mercadopago.plugins;

import android.support.annotation.NonNull;

import com.mercadopago.core.CheckoutStore;

import java.util.Map;

public abstract class DataInitializationTask {

    private final Map<String, Object> data;
    private Thread taskThread;

    public DataInitializationTask(@NonNull final Map<String, Object> defaultData) {
        this.data = CheckoutStore.getInstance().getData();
        this.data.clear();
        this.data.putAll(defaultData);
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
            taskThread.interrupt();
        }
    }

    public abstract void onLoadData(@NonNull final Map<String, Object> data);

    public interface DataInitializationCallbacks {
        void onDataInitialized(@NonNull final Map<String, Object> data);
    }
}