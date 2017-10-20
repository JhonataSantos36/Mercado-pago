package com.mercadopago.tracking.strategies;

import android.content.Context;

import com.mercadopago.tracking.model.Event;
import com.mercadopago.tracking.model.EventTrackIntent;
import com.mercadopago.tracking.services.MPTrackingService;

import java.util.ArrayList;
import java.util.List;

public class RealTimeTrackingStrategy extends TrackingStrategy {
    private final MPTrackingService trackingService;

    public RealTimeTrackingStrategy(MPTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @Override
    public void trackEvent(Event event, Context context) {
        List<Event> events = new ArrayList<>();
        events.add(event);
        EventTrackIntent eventTrackIntent = new EventTrackIntent(getClientId(), getAppInformation(), getDeviceInfo(), events);
        trackingService.trackEvents(eventTrackIntent, context);
    }
}
