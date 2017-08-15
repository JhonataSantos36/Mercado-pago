package com.mercadopago.px_tracking.strategies;

import android.content.Context;
import android.util.Log;

import com.mercadopago.px_tracking.model.Event;
import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.services.MPTrackingService;

import java.sql.Timestamp;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BatchTrackingStrategy extends TrackingStrategy {

    private static final long MAX_AGEING = 15;

    private final MPTrackingService trackingService;
    private final ConnectivityChecker connectivityChecker;

    public BatchTrackingStrategy(EventsDatabase database, ConnectivityChecker connectivityChecker, MPTrackingService trackingService) {
        setDatabase(database);
        this.trackingService = trackingService;
        this.connectivityChecker = connectivityChecker;
    }

    @Override
    public void trackEvent(Event event, Context context) {
        performTrackAttempt(context);
    }

    private void performTrackAttempt(Context context) {
        if (shouldSendBatch()) {
            sendTracksBatch(context);
        }
    }

    private boolean shouldSendBatch() {
        return isConnectivityOk() && isDataAvailable() && isDataReady();
    }

    private boolean isConnectivityOk() {
        return connectivityChecker.hasWifiConnection();
    }

    private boolean isDataReady() {
        return getNextTrackAge() >= MAX_AGEING;
    }

    private void sendTracksBatch(final Context context) {
        final List<Event> batch = getDatabase().retrieveBatch();
        EventTrackIntent intent = new EventTrackIntent(getClientId(), getAppInformation(), getDeviceInfo(), batch);
        trackingService.trackEvents(intent, context, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Log.v("BATCH","200");
                } else {
                    getDatabase().returnEvents(batch);

                    if (response.code() == 513) {
                        performTrackAttempt(context);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                getDatabase().returnEvents(batch);
            }
        });
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public long getNextTrackAge() {
        long result = (getCurrentTimestamp().getTime() - getDatabase().getNextTrackTimestamp().getTime()) / 1000;
        return result;
    }
}
