package com.mercadopago.preferences;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.constants.ProcessingModes;

import java.util.HashMap;
import java.util.Map;

public class ServicePreference {

    @SerializedName("default_base_url")
    private final String defaultBaseURL;

    @SerializedName("gateway_base_url")
    private final String gatewayBaseURL;

    @SerializedName("get_customer_url")
    private final String getCustomerURL;

    @SerializedName("create_payment_url")
    private final String createPaymentURL;

    @SerializedName("create_checkout_preference_url")
    private final String createCheckoutPreferenceURL;

    @SerializedName("get_merchant_discount_url")
    private final String getMerchantDiscountBaseURL;

    @SerializedName("get_customer_uri")
    private final String getCustomerURI;

    @SerializedName("create_payment_uri")
    private final String createPaymentURI;

    @SerializedName("create_checkout_preference_uri")
    private final String createCheckoutPreferenceURI;

    @SerializedName("get_merchant_discount_uri")
    private final String getMerchantDiscountURI;

    @SerializedName("processing_mode")
    private final String processingMode;

    private Map<String, String> getCustomerAdditionalInfo;
    private Map<String, Object> createPaymentAdditionalInfo;
    private Map<String, Object> createCheckoutPreferenceAdditionalInfo;
    private Map<String, String> getDiscountAdditionalInfo;

    private ServicePreference(Builder builder) {

        defaultBaseURL = builder.defaultBaseURL;
        gatewayBaseURL = builder.gatewayBaseURL;

        getCustomerURL = builder.getCustomerURL;
        createPaymentURL = builder.createPaymentURL;
        createCheckoutPreferenceURL = builder.createCheckoutPreferenceURL;
        getMerchantDiscountBaseURL = builder.getMerchantDiscountBaseURL;
        getCustomerURI = builder.getCustomerURI;
        createPaymentURI = builder.createPaymentURI;
        createCheckoutPreferenceURI = builder.createCheckoutPreferenceURI;
        getMerchantDiscountURI = builder.getMerchantDiscountURI;
        getCustomerAdditionalInfo = builder.getCustomerAdditionalInfo;
        createPaymentAdditionalInfo = builder.createPaymentAdditionalInfo;
        createCheckoutPreferenceAdditionalInfo = builder.createCheckoutPreferenceAdditionalInfo;
        getDiscountAdditionalInfo = builder.getDiscountAdditionalInfo;
        processingMode = builder.processingMode;
    }

    public String getDefaultBaseURL() {
        return defaultBaseURL;
    }

    public String getGatewayBaseURL() {
        return gatewayBaseURL;
    }

    public String getGetCustomerURL() {
        return getCustomerURL;
    }

    public String getCreatePaymentURL() {
        return createPaymentURL;
    }

    public String getCreateCheckoutPreferenceURL() {
        return createCheckoutPreferenceURL;
    }

    public String getGetMerchantDiscountBaseURL() {
        return getMerchantDiscountBaseURL;
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

    public String getGetMerchantDiscountURI() {
        return getMerchantDiscountURI;
    }

    public String getProcessingModeString() { return processingMode; }

    public Map<String, String> getGetCustomerAdditionalInfo() {
        if (getCustomerAdditionalInfo == null) {
            getCustomerAdditionalInfo = new HashMap<>();
        }
        return getCustomerAdditionalInfo;
    }

    public Map<String, Object> getCreatePaymentAdditionalInfo() {
        if (createPaymentAdditionalInfo == null) {
            createPaymentAdditionalInfo = new HashMap<>();
        }
        return createPaymentAdditionalInfo;
    }

    public Map<String, Object> getCreateCheckoutPreferenceAdditionalInfo() {
        if (createCheckoutPreferenceAdditionalInfo == null) {
            createCheckoutPreferenceAdditionalInfo = new HashMap<>();
        }
        return createCheckoutPreferenceAdditionalInfo;
    }

    public Map<String, String> getGetDiscountAdditionalInfo() {
        if (getDiscountAdditionalInfo == null) {
            getDiscountAdditionalInfo = new HashMap<>();
        }
        return getDiscountAdditionalInfo;
    }

    public boolean hasGetCustomerURL() {
        return getCustomerURL != null && getCustomerURI != null;
    }

    public boolean hasCreatePaymentURL() {
        return createPaymentURL != null && createPaymentURI != null;
    }

    public boolean hasCreateCheckoutPrefURL() {
        return createCheckoutPreferenceURL != null && createCheckoutPreferenceURI != null;
    }

    public boolean hasGetDiscountURL() {
        return (getMerchantDiscountBaseURL != null || defaultBaseURL != null) && getMerchantDiscountURI != null;
    }

    public boolean shouldShowBankDeals() {
        return processingMode.equals(ProcessingModes.AGGREGATOR);
    }

    public boolean shouldShowEmailConfirmationCell() {
        return processingMode.equals(ProcessingModes.AGGREGATOR);
    }

    public static class Builder {

        private String defaultBaseURL;
        private String gatewayBaseURL;

        private String getCustomerURL;
        private String createPaymentURL;
        private String createCheckoutPreferenceURL;
        private String getMerchantDiscountBaseURL;
        private String getCustomerURI;
        private String createPaymentURI;
        private String createCheckoutPreferenceURI;
        private String getMerchantDiscountURI;
        private String processingMode;
        private Map<String, String> getCustomerAdditionalInfo;
        private Map<String, Object> createPaymentAdditionalInfo;
        private Map<String, Object> createCheckoutPreferenceAdditionalInfo;
        private Map<String, String> getDiscountAdditionalInfo;

        public Builder setGetCustomerURL(String getCustomerURL, String getCustomerURI) {
            this.getCustomerURL = getCustomerURL;
            this.getCustomerURI = getURI(getCustomerURI);
            return this;
        }

        public Builder setGetCustomerURL(String getCustomerURL, String getCustomerURI, Map<String, String> additionalInfo) {
            this.getCustomerURL = getCustomerURL;
            this.getCustomerURI = getURI(getCustomerURI);
            getCustomerAdditionalInfo = additionalInfo;
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
            createPaymentAdditionalInfo = additionalInfo;
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
            createCheckoutPreferenceAdditionalInfo = additionalInfo;
            return this;
        }

        public Builder setDiscountURL(String getMerchantDiscountBaseURL, String getMerchantDiscountURI, Map<String, String> additionalInfo) {
            this.getMerchantDiscountBaseURL = getMerchantDiscountBaseURL;
            this.getMerchantDiscountURI = getMerchantDiscountURI;
            getDiscountAdditionalInfo = additionalInfo;
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

        public Builder setAggregatorAsProcessingMode() {
            processingMode = ProcessingModes.AGGREGATOR;
            return this;
        }

        public Builder setGatewayAsProcessingMode() {
            processingMode = ProcessingModes.GATEWAY;
            return this;
        }

        public Builder setHybridAsProcessingMode() {
            processingMode = ProcessingModes.HYBRID;
            return this;
        }

        public ServicePreference build() {
            if (processingMode == null) {
                processingMode = ProcessingModes.AGGREGATOR;
            }
            return new ServicePreference(this);
        }

        private String getURI(String uri) {
            return uri.startsWith("/") ? uri.substring(1) : uri;
        }
    }
}
