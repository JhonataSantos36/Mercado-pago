package com.mercadopago.tracking.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaserber on 6/5/17.
 */

public class ScreenViewEvent extends Event {

    private String screenId;
    private String screenName;

    protected ScreenViewEvent() {

    }

    private ScreenViewEvent(Builder builder) {
        super();
        setType(TYPE_SCREEN_VIEW);
        setTimestamp(System.currentTimeMillis());
        setProperties(builder.properties);
        setFlowId(builder.flowId);
        this.screenId = builder.screenId;
        this.screenName = builder.screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getScreenId() {
        return screenId;
    }

    public static class Builder {

        private String flowId;
        private String screenId;
        private String screenName;
        private Map<String, String> properties = new HashMap<>();

        public Builder setFlowId(String flowId) {
            this.flowId = flowId;
            return this;
        }

        public Builder setScreenId(String screenId) {
            this.screenId = screenId;
            return this;
        }

        public Builder setScreenName(String screenName) {
            this.screenName = screenName;
            return this;
        }

        public Builder addProperty(String key, String value) {
            properties.put(key, value);
            return this;
        }

        public ScreenViewEvent build() {
            return new ScreenViewEvent(this);
        }
    }
}
