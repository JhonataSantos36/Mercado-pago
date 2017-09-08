package com.mercadopago.preferences;

import com.google.gson.annotations.SerializedName;
import com.mercadopago.constants.ProcessingModes;

import java.util.HashMap;
import java.util.Map;

public class ServicePreference {

    @SerializedName("default_base_url")
    private String defaultBaseURL;

    @SerializedName("gateway_base_url")
    private String gatewayBaseURL;

    @SerializedName("get_customer_url")
    private String getCustomerURL;

    @SerializedName("create_payment_url")
    private String createPaymentURL;

    @SerializedName("create_checkout_preference_url")
    private String createCheckoutPreferenceURL;

    @SerializedName("get_merchant_discount_url")
    private String getMerchantDiscountBaseURL;

    @SerializedName("get_customer_uri")
    private String getCustomerURI;

    @SerializedName("create_payment_uri")
    private String createPaymentURI;

    @SerializedName("create_checkout_preference_uri")
    private String createCheckoutPreferenceURI;

    @SerializedName("get_merchant_discount_uri")
    private String getMerchantDiscountURI;

    @SerializedName("processing_mode")
    private String processingMode;

    private Map<String, String> getCustomerAdditionalInfo;
    private Map<String, Object> createPaymentAdditionalInfo;
    private Map<String, Object> createCheckoutPreferenceAdditionalInfo;
    private Map<String, String> getDiscountAdditionalInfo;

    private ServicePreference(Builder builder) {

        this.defaultBaseURL = builder.defaultBaseURL;
        this.gatewayBaseURL = builder.gatewayBaseURL;

        this.getCustomerURL = builder.getCustomerURL;
        this.createPaymentURL = builder.createPaymentURL;
        this.createCheckoutPreferenceURL = builder.createCheckoutPreferenceURL;
        this.getMerchantDiscountBaseURL = builder.getMerchantDiscountBaseURL;
        this.getCustomerURI = builder.getCustomerURI;
        this.createPaymentURI = builder.createPaymentURI;
        this.createCheckoutPreferenceURI = builder.createCheckoutPreferenceURI;
        this.getMerchantDiscountURI = builder.getMerchantDiscountURI;
        this.getCustomerAdditionalInfo = builder.getCustomerAdditionalInfo;
        this.createPaymentAdditionalInfo = builder.createPaymentAdditionalInfo;
        this.createCheckoutPreferenceAdditionalInfo = builder.createCheckoutPreferenceAdditionalInfo;
        this.getDiscountAdditionalInfo = builder.getDiscountAdditionalInfo;
        this.processingMode = builder.processingMode;
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

    public String getGetMerchantDiscountBaseURL() {
        return this.getMerchantDiscountBaseURL;
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

    public Map<String, String> getGetDiscountAdditionalInfo() {
        if (this.getDiscountAdditionalInfo == null) {
            this.getDiscountAdditionalInfo = new HashMap<>();
        }
        return this.getDiscountAdditionalInfo;
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

        public Builder setDiscountURL(String getMerchantDiscountBaseURL, String getMerchantDiscountURI, Map<String, String> additionalInfo) {
            this.getMerchantDiscountBaseURL = getMerchantDiscountBaseURL;
            this.getMerchantDiscountURI = getMerchantDiscountURI;
            this.getDiscountAdditionalInfo = additionalInfo;
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
            this.processingMode = ProcessingModes.AGGREGATOR;
            return this;
        }

        public Builder setGatewayAsProcessingMode() {
            this.processingMode = ProcessingModes.GATEWAY;
            return this;
        }

        public Builder setHybridAsProcessingMode() {
            this.processingMode = ProcessingModes.HYBRID;
            return this;
        }

        public ServicePreference build() {
            if (this.processingMode == null) {
                this.processingMode = ProcessingModes.AGGREGATOR;
            }
            return new ServicePreference(this);
        }

        private String getURI(String uri) {
            return uri.startsWith("/") ? uri.substring(1) : uri;
        }
    }
}
