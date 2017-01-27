package com.mercadopago.preferences;

import android.net.Uri;

import java.util.Map;

/**
 * Created by mreverter on 1/17/17.
 */
public class ServicePreference {

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
        return this.getCustomerAdditionalInfo;
    }

    public Map<String, Object> getCreatePaymentAdditionalInfo() {
        return this.createPaymentAdditionalInfo;
    }

    public Map<String, Object> getCreateCheckoutPreferenceAdditionalInfo() {
        return this.createCheckoutPreferenceAdditionalInfo;
    }

    public static class Builder {

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

        public ServicePreference build() {
            return new ServicePreference(this);
        }

        //TODO validar
//        private String getBaseURL(String url) {
//            String base = Uri.parse(url).getAuthority();
//            String protocol = Uri.parse(url).getScheme();
//            return protocol + "://" + base;
//        }

//        private String getURI(String url) {
//            String uri = Uri.parse(url).getPath();
//            return uri.startsWith("/") ? uri.substring(1) : uri;
//        }

        private String getURI(String uri) {
            return uri.startsWith("/") ? uri.substring(1) : uri;
        }
    }

}
