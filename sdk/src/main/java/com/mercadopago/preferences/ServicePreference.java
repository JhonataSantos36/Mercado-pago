package com.mercadopago.preferences;

import android.net.Uri;

import com.mercadopago.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mreverter on 1/17/17.
 */
public class ServicePreference {

    public static final String DEFAULT_CREATE_PAYMENT_URI = "/" + BuildConfig.API_VERSION + "/checkout/payments";
    public static final String DEFAULT_CREATE_PAYMENT_URL = "https://api.mercadopago.com";

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
