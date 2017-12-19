package com.mercadopago.tracking.strategies;

import android.content.Context;

import com.mercadopago.tracking.model.AppInformation;
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
        //Adapt to service v2.
        AppInformation appInformation = getAppInformation().copy();
        appInformation.setFlowId(event.getFlowId());
        EventTrackIntent eventTrackIntent = new EventTrackIntent(appInformation, getDeviceInfo(), events);
        trackingService.trackEvents(getPublicKey(), eventTrackIntent, context);
    }
}
