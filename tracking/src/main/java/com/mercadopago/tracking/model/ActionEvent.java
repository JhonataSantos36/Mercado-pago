package com.mercadopago.tracking.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaserber on 6/5/17.
 */

public class ActionEvent extends Event {

    private String screenId;
    private String screenName;
    private String action;
    private String category;
    private String label;
    private String value;

    private ActionEvent(Builder builder) {
        super();
        setType(TYPE_ACTION);
        setFlowId(builder.flowId);
        setTimestamp(System.currentTimeMillis());
        setProperties(builder.properties);
        this.screenId = builder.screenId;
        this.screenName = builder.screenName;
        this.action = builder.action;
        this.category = builder.category;
        this.label = builder.label;
        this.value = builder.value;
    }

    public String getScreenId() {
        return screenId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getAction() {
        return action;
    }

    public String getCategory() {
        return category;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {

        private String flowId;
        private String screenId;
        private String screenName;
        private String action;
        private String category;
        private String label;
        private String value;
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

        public Builder setAction(String action) {
            this.action = action;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder addProperty(String key, String value) {
            properties.put(key, value);
            return this;
        }

        public ActionEvent build() {
            return new ActionEvent(this);
        }
    }
}
