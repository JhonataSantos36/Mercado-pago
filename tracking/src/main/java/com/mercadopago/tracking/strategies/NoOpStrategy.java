package com.mercadopago.tracking.strategies;

import android.content.Context;

import com.mercadopago.tracking.model.Event;

public class NoOpStrategy extends TrackingStrategy {

    @Override
    public void trackEvent(Event event, Context context) {
        // This strategy does not track any event
    }
}