package com.mercadopago.px_tracking.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 6/5/17.
 */

public class EventTrackIntent {

    private String clientId;
    private AppInformation application;
    private DeviceInfo device;
    private List<Event> events;

    public EventTrackIntent(String clientId, AppInformation application, DeviceInfo device, List<Event> events) {
        this.clientId = clientId;
        this.application = application;
        this.device = device;
        this.events = events;
    }

    public String getClientId() {
        return clientId;
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
