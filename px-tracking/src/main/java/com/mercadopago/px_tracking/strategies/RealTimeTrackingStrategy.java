package com.mercadopago.px_tracking.strategies;

import android.content.Context;

import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.services.MPTrackingService;

public class RealTimeTrackingStrategy implements TrackingStrategy {

    private final MPTrackingService trackingService;

    public RealTimeTrackingStrategy(MPTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @Override
    public void trackEvents(EventTrackIntent eventTrackIntent, Context context) {
        trackingService.trackEvent(eventTrackIntent, context);
    }
}
