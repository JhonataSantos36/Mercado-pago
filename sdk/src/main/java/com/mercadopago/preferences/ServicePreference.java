package com.mercadopago.preferences;

import java.util.HashMap;
import java.util.Map;

public class ServicePreference {

    private String defaultBaseURL;
    private String gatewayBaseURL;

    private String getCustomerURL;
    private String createPaymentURL;
    private String createCheckoutPreferenceURL;
    private String getCustomerURI;
    private String createPaymentURI;
    private String createCheckoutPreferenceURI;
    private Map<String, String> getCustomerAdditionalInfo;
    private Map<String, Object> createPaymentAdditionalInfo;
    private Map<String, Object> createCheckoutPreferenceAdditionalInfo;

    private ServicePreference(Builder builder) {

        this.defaultBaseURL = builder.defaultBaseURL;
        this.gatewayBaseURL = builder.gatewayBaseURL;

        this.getCustomerURL = builder.getCustomerURL;
        this.createPaymentURL = builder.createPaymentURL;
        this.createCheckoutPreferenceURL = builder.createCheckoutPreferenceURL;
        this.getCustomerURI = builder.getCustomerURI;
        this.createPaymentURI = builder.createPaymentURI;
        this.createCheckoutPreferenceURI = builder.createCheckoutPreferenceURI;
        this.getCustomerAdditionalInfo = builder.getCustomerAdditionalInfo;
        this.createPaymentAdditionalInfo = builder.createPaymentAdditionalInfo;
        this.createCheckoutPreferenceAdditionalInfo = builder.createCheckoutPreferenceAdditionalInfo;
    }

    public String getDefaultBaseURL() {
        return defaultBaseURL;
    }

    public String getGatewayBaseURL() {
        return gatewayBaseURL;
    }

    public String getGetCustomerURL() {
        return this.getCustomerURL;
    }

    public String getCreatePaymentURL() {
        return this.createPaymentURL;
    }

    public String getCreateCheckoutPreferenceURL() {
        return this.createCheckoutPreferenceURL;
    }

    public String getGetCustomerURI() {
        return getCustomerURI;
    }

    public String getCreatePaymentURI() {
        return createPaymentURI;
    }

    public String getCreateCheckoutPreferenceURI() {
        return createCheckoutPreferenceURI;
    }

    public Map<String, String> getGetCustomerAdditionalInfo() {
        if (this.getCustomerAdditionalInfo == null) {
            this.getCustomerAdditionalInfo = new HashMap<>();
        }
        return this.getCustomerAdditionalInfo;
    }

    public Map<String, Object> getCreatePaymentAdditionalInfo() {
        if (this.createPaymentAdditionalInfo == null) {
          this.createPaymentAdditionalInfo = new HashMap<>();
        }
        return this.createPaymentAdditionalInfo;
    }

    public Map<String, Object> getCreateCheckoutPreferenceAdditionalInfo() {
        if (this.createCheckoutPreferenceAdditionalInfo == null) {
            this.createCheckoutPreferenceAdditionalInfo = new HashMap<>();
        }
        return this.createCheckoutPreferenceAdditionalInfo;
    }

    public boolean hasGetCustomerURL() {
        return getCustomerURL != null && getCustomerURI != null;
    }

    public boolean hasCreatePaymentURL() {
        return createPaymentURL != null && createPaymentURI != null;
    }

    public static class Builder {

        private String defaultBaseURL;
        private String gatewayBaseURL;

        private String getCustomerURL;
        private String createPaymentURL;
        private String createCheckoutPreferenceURL;
        private String getCustomerURI;
        private String createPaymentURI;
        private String createCheckoutPreferenceURI;
        private Map<String, String> getCustomerAdditionalInfo;
        private Map<String, Object> createPaymentAdditionalInfo;
        private Map<String, Object> createCheckoutPreferenceAdditionalInfo;

        public Builder setGetCustomerURL(String getCustomerURL, String getCustomerURI) {
            this.getCustomerURL = getCustomerURL;
            this.getCustomerURI = getURI(getCustomerURI);
            return this;
        }

        public Builder setGetCustomerURL(String getCustomerURL, String getCustomerURI, Map<String, String> additionalInfo) {
            this.getCustomerURL = getCustomerURL;
            this.getCustomerURI = getURI(getCustomerURI);
            this.getCustomerAdditionalInfo = additionalInfo;
            return this;
        }

        public Builder setCreatePaymentURL(String createPaymentURL, String createPaymentURI) {
            this.createPaymentURL = createPaymentURL;
            this.createPaymentURI = getURI(createPaymentURI);
            return this;
        }

        public Builder setCreatePaymentURL(String createPaymentURL, String createPaymentURI, Map<String, Object> additionalInfo) {
            this.createPaymentURL = createPaymentURL;
            this.createPaymentURI = getURI(createPaymentURI);
            this.createPaymentAdditionalInfo = additionalInfo;
            return this;
        }

        public Builder setCreateCheckoutPreferenceURL(String createCheckoutPreferenceURL, String createCheckoutPreferenceURI) {
            this.createCheckoutPreferenceURL = createCheckoutPreferenceURL;
            this.createCheckoutPreferenceURI = getURI(createCheckoutPreferenceURI);
            return this;
        }

        public Builder setCreateCheckoutPreferenceURL(String createCheckoutPreferenceURL, String createCheckoutPreferenceURI, Map<String, Object> additionalInfo) {
            this.createCheckoutPreferenceURL = createCheckoutPreferenceURL;
            this.createCheckoutPreferenceURI = getURI(createCheckoutPreferenceURI);
            this.createCheckoutPreferenceAdditionalInfo = additionalInfo;
            return this;
        }

        public Builder setDefaultBaseURL(String defaultBaseURL) {
            this.defaultBaseURL = defaultBaseURL;
            return this;
        }

        public Builder setGatewayURL(String gatewayBaseURL) {
            this.gatewayBaseURL = gatewayBaseURL;
            return this;
        }

        public ServicePreference build() {
            return new ServicePreference(this);
        }

        private String getURI(String uri) {
            return uri.startsWith("/") ? uri.substring(1) : uri;
        }
    }
}
