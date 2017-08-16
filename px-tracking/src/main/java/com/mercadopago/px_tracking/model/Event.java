package com.mercadopago.px_tracking.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Timestamp;
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


    private Timestamp timestamp;
    private @EventType String type;
    private Map<String, String> metadata;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public @EventType String getType() {
        return type;
    }

    protected void setType(@EventType String type) {
        this.type = type;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.putAll(metadata);
    }
}
