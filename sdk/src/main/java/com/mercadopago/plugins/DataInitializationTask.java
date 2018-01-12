package com.mercadopago.plugins;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.mercadopago.core.CheckoutStore;

import java.util.HashMap;
import java.util.Map;

public abstract class DataInitializationTask extends AsyncTask<Void, Void, Map<String, Object>> {

    private DataInitializationListener listener;
    protected Map<String, Object> data = new HashMap<>();

    public DataInitializationTask(@NonNull final Map<String, Object> data) {
        this.data = data;
    }

    @Override
    protected Map<String, Object> doInBackground(Void... voids) {
        return initializeData();
    }

    protected void onPostExecute(@NonNull final Map<String, Object> data) {
        CheckoutStore.getInstance().setData(data);
        if (listener != null) {
            listener.onDataInitialized(data);
        }
    }

    public DataInitializationTask setListener(@NonNull final DataInitializationListener listener) {
        this.listener = listener;
        return this;
    }

    public abstract Map<String, Object> initializeData();
    public abstract void onFailure(@NonNull final Exception e);

    public interface DataInitializationListener {
        void onDataInitialized(@NonNull final Map<String, Object> data);
    }
}