package com.mercadopago.tracking.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaserber on 6/5/17.
 */

public abstract class Event {

    //Event Types
    public static final String TYPE_SCREEN_VIEW = "screenview";
    public static final String TYPE_ACTION = "action";
    public static final String TYPE_ERROR = "error";

    @StringDef({TYPE_SCREEN_VIEW, TYPE_ACTION, TYPE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {}


    private String flowId;
    private Long timestamp;
    private @EventType String type;
    private Map<String, String> properties;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public @EventType String getType() {
        return type;
    }

    protected void setType(@EventType String type) {
        this.type = type;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.putAll(properties);
    }
}
