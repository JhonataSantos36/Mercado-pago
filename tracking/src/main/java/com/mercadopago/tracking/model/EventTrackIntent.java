package com.mercadopago.tracking.model;

import java.util.List;

/**
 * Created by vaserber on 6/5/17.
 */

public class EventTrackIntent {

    private AppInformation application;
    private DeviceInfo device;
    private List<Event> events;

    public EventTrackIntent(AppInformation application, DeviceInfo device, List<Event> events) {
        this.application = application;
        this.device = device;
        this.events = events;
    }

    public AppInformation getApplication() {
        return application;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public List<Event> getEvents() {
        return events;
    }

}
