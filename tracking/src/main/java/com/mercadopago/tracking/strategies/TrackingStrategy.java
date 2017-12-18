package com.mercadopago.tracking.strategies;

import android.content.Context;

import com.mercadopago.tracking.model.AppInformation;
import com.mercadopago.tracking.model.DeviceInfo;
import com.mercadopago.tracking.model.Event;
import com.mercadopago.tracking.model.EventTrackIntent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TrackingStrategy {

    private String publicKey;
    private AppInformation appInformation;
    private DeviceInfo deviceInfo;
    private EventsDatabase database;

    public abstract void trackEvent(Event event, Context context);

    public void setAppInformation(AppInformation appInformation) {
        this.appInformation = appInformation;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public AppInformation getAppInformation() {
        return appInformation;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public EventsDatabase getDatabase() {
        return database;
    }

    public void setDatabase(EventsDatabase database) {
        this.database = database;
    }

    public boolean isDataAvailable() {
        return database != null && database.getBatchSize() != 0;
    }

    //Method to generate different requests per flow ID
    public List<EventTrackIntent> groupEventsByFlow(List<Event> events) {
        Map<String, List<Event>> eventsByFlowMap = mapEventsToFlow(events);
        List<EventTrackIntent> eventTrackIntents = new ArrayList<>();

        for(String flowId : eventsByFlowMap.keySet()) {
            AppInformation appInformation = getAppInformation().copy();
            appInformation.setFlowId(flowId);
            eventTrackIntents.add(new EventTrackIntent(appInformation, getDeviceInfo(), eventsByFlowMap.get(flowId)));
        }
        return eventTrackIntents;
    }

    private Map<String, List<Event>> mapEventsToFlow(List<Event> events) {
        Map<String, List<Event>> eventsPerFlowMap = new HashMap<>();
        for (Event event : events) {
            List<Event> currentEvents = eventsPerFlowMap.get(event.getFlowId());
            if(currentEvents == null) {
                currentEvents = new ArrayList<>();
                eventsPerFlowMap.put(event.getFlowId(), currentEvents);
            }
            currentEvents.add(event);
        }
        return eventsPerFlowMap;
    }
}
