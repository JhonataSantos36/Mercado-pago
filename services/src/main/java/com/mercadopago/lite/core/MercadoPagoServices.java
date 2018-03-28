package com.mercadopago.lite.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.mercadopago.lite.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.lite.constants.ProcessingModes;
import com.mercadopago.lite.controllers.CustomServicesHandler;
import com.mercadopago.lite.model.BankDeal;
import com.mercadopago.lite.model.Campaign;
import com.mercadopago.lite.model.CardToken;
import com.mercadopago.lite.model.Customer;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.model.IdentificationType;
import com.mercadopago.lite.model.Installment;
import com.mercadopago.lite.model.Instructions;
import com.mercadopago.lite.model.Issuer;
import com.mercadopago.lite.model.Payer;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.PaymentMethodSearch;
import com.mercadopago.lite.model.SavedCardToken;
import com.mercadopago.lite.model.SavedESCCardToken;
import com.mercadopago.lite.model.Site;
import com.mercadopago.lite.model.Token;
import com.mercadopago.lite.model.requests.PayerIntent;
import com.mercadopago.lite.model.requests.SecurityCodeIntent;
import com.mercadopago.lite.preferences.CheckoutPreference;
import com.mercadopago.lite.preferences.ServicePreference;
import com.mercadopago.lite.services.BankDealService;
import com.mercadopago.lite.services.CheckoutService;
import com.mercadopago.lite.services.CustomService;
import com.mercadopago.lite.services.DiscountService;
import com.mercadopago.lite.services.GatewayService;
import com.mercadopago.lite.services.IdentificationService;
import com.mercadopago.lite.services.PaymentService;
import com.mercadopago.lite.util.HttpClientUtil;
import com.mercadopago.lite.util.JsonUtil;
import com.mercadopago.lite.util.TextUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MercadoPagoServices {

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";

    private static final String PAYMENT_RESULT_API_VERSION = "1.4";
    private static final String PAYMENT_METHODS_OPTIONS_API_VERSION = "1.5";

    public static final int DEFAULT_CONNECT_TIMEOUT = 10;
    public static final int DEFAULT_READ_TIMEOUT = 20;
    public static final int DEFAULT_WRITE_TIMEOUT = 20;

    public static final int DEFAULT_PAYMENT_CONNECT_TIMEOUT = 10;
    public static final int DEFAULT_PAYMENT_READ_TIMEOUT = 20;
    public static final int DEFAULT_PAYMENT_WRITE_TIMEOUT = 20;

    private ServicePreference mServicePreference;
    private String mPublicKey;
    private String mPrivateKey;
    private Context mContext;
    private String mProcessingMode;
    private Boolean mBetaEnvironment;

    private MercadoPagoServices(Builder builder) {
        this.mContext = builder.mContext;
        this.mPublicKey = builder.mPublicKey;
        this.mPrivateKey = builder.mPrivateKey;
        this.mServicePreference = CustomServicesHandler.getInstance().getServicePreference();
        this.mProcessingMode = mServicePreference != null ? mServicePreference.getProcessingModeString() : ProcessingModes.AGGREGATOR;
        this.mBetaEnvironment = builder.mBetaEnvironment;
        disableConnectionReuseIfNecessary();
    }

    public void getCheckoutPreference(String checkoutPreferenceId, Callback<CheckoutPreference> callback) {
        CheckoutService service = getDefaultRetrofit().create(CheckoutService.class);
        service.getPreference(Settings.servicesVersion, checkoutPreferenceId, this.mPublicKey).enqueue(callback);
    }

    public void getInstructions(Long paymentId, String paymentTypeId, final Callback<Instructions> callback) {
        CheckoutService service = getDefaultRetrofit().create(CheckoutService.class);
        service.getPaymentResult(Settings.servicesVersion, mContext.getResources().getConfiguration().locale.getLanguage(), paymentId, this.mPublicKey, this.mPrivateKey, paymentTypeId, PAYMENT_RESULT_API_VERSION).enqueue(callback);
    }

    public void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, Site site, final Callback<PaymentMethodSearch> callback) {
        PayerIntent payerIntent = new PayerIntent(payer);
        CheckoutService service = getDefaultRetrofit().create(CheckoutService.class);

        String separator = ",";
        String excludedPaymentTypesAppended = getListAsString(excludedPaymentTypes, separator);
        String excludedPaymentMethodsAppended = getListAsString(excludedPaymentMethods, separator);
        String siteId = site == null ? "" : site.getId();
        service.getPaymentMethodSearch(Settings.servicesVersion, mContext.getResources().getConfiguration().locale.getLanguage(), this.mPublicKey, amount, excludedPaymentTypesAppended, excludedPaymentMethodsAppended, payerIntent, siteId, PAYMENT_METHODS_OPTIONS_API_VERSION, mProcessingMode).enqueue(callback);
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedCardToken.setDevice(mContext);
                GatewayService service = getGatewayRetrofit().create(GatewayService.class);
                service.getToken(mPublicKey, mPrivateKey, savedCardToken).enqueue(callback);
            }
        }).start();
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cardToken.setDevice(mContext);
                GatewayService service = getGatewayRetrofit().create(GatewayService.class);
                service.getToken(mPublicKey, mPrivateKey, cardToken).enqueue(callback);
            }
        }).start();
    }

    public void createToken(final SavedESCCardToken savedESCCardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedESCCardToken.setDevice(mContext);
                GatewayService service = getGatewayRetrofit().create(GatewayService.class);
                service.getToken(mPublicKey, mPrivateKey, savedESCCardToken).enqueue(callback);
            }
        }).start();
    }

    public void cloneToken(final String tokenId, final Callback<Token> callback) {
        GatewayService service = getGatewayRetrofit().create(GatewayService.class);
        service.getToken(tokenId, this.mPublicKey, mPrivateKey).enqueue(callback);
    }

    public void putSecurityCode(final String tokenId, final SecurityCodeIntent securityCodeIntent, final Callback<Token> callback) {
        GatewayService service = getGatewayRetrofit().create(GatewayService.class);
        service.getToken(tokenId, this.mPublicKey, mPrivateKey, securityCodeIntent).enqueue(callback);
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        BankDealService service = getDefaultRetrofit().create(BankDealService.class);
        service.getBankDeals(this.mPublicKey, mPrivateKey, mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
    }

    public void getIdentificationTypes(Callback<List<IdentificationType>> callback) {
        IdentificationService service = getDefaultRetrofit().create(IdentificationService.class);
        service.getIdentificationTypes(this.mPublicKey, this.mPrivateKey).enqueue(callback);
    }

    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, Callback<List<Installment>> callback) {
        PaymentService service = getDefaultRetrofit().create(PaymentService.class);
        service.getInstallments(Settings.servicesVersion, this.mPublicKey, mPrivateKey, bin, amount, issuerId, paymentMethodId,
                mContext.getResources().getConfiguration().locale.toString(), mProcessingMode).enqueue(callback);
    }

    public void getIssuers(String paymentMethodId, String bin, final Callback<List<Issuer>> callback) {
        PaymentService service = getDefaultRetrofit().create(PaymentService.class);
        service.getIssuers(Settings.servicesVersion, this.mPublicKey, mPrivateKey, paymentMethodId, bin, mProcessingMode).enqueue(callback);
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        PaymentService service = getDefaultRetrofit().create(PaymentService.class);
        service.getPaymentMethods(this.mPublicKey, mPrivateKey).enqueue(callback);
    }

    public void getDirectDiscount(String amount, String payerEmail, final Callback<Discount> callback) {
        DiscountService service = getDefaultRetrofit().create(DiscountService.class);
        service.getDirectDiscount(this.mPublicKey, amount, payerEmail).enqueue(callback);
    }

    public void getCodeDiscount(String amount, String payerEmail, String couponCode, final Callback<Discount> callback) {
        DiscountService service = getDefaultRetrofit().create(DiscountService.class);
        service.getCodeDiscount(this.mPublicKey, amount, payerEmail, couponCode).enqueue(callback);
    }

    public void getCampaigns(final Callback<List<Campaign>> callback) {
        DiscountService service = getDefaultRetrofit().create(DiscountService.class);
        service.getCampaigns(this.mPublicKey).enqueue(callback);
    }

    public void createCheckoutPreference(String url, String uri, Callback<CheckoutPreference> callback) {
        CustomService customService = getCustomService(url);
        customService.createPreference(uri, null).enqueue(callback);
    }

    public void createCheckoutPreference(String url, String uri, Map<String, Object> bodyInfo, Callback<CheckoutPreference> callback) {
        CustomService customService = getCustomService(url);
        customService.createPreference(uri, bodyInfo).enqueue(callback);
    }

    public void getCustomer(String url, String uri, Callback<Customer> callback) {
        CustomService customService = getCustomService(url);
        customService.getCustomer(uri, null).enqueue(callback);
    }

    public void getCustomer(String url, String uri, @NonNull Map<String, String> additionalInfo, Callback<Customer> callback) {
        if (additionalInfo == null) {
            additionalInfo = new HashMap<>();
        }

        CustomService customService = getCustomService(url);
        customService.getCustomer(uri, additionalInfo).enqueue(callback);
    }

    public void createPayment(String baseUrl, String uri, Map<String, Object> paymentData, @NonNull Map<String, String> query, Callback<Payment> callback) {
        if (query == null) {
            query = new HashMap<>();
        }
        CustomService customService = getCustomService(baseUrl, DEFAULT_PAYMENT_CONNECT_TIMEOUT, DEFAULT_PAYMENT_READ_TIMEOUT, DEFAULT_PAYMENT_WRITE_TIMEOUT);
        customService.createPayment(Settings.servicesVersion, ripFirstSlash(uri), paymentData, query).enqueue(callback);
    }

    public void createPayment(String transactionId, String baseUrl, String uri,
                              Map<String, Object> paymentData, @NonNull Map<String, String> query, Callback<Payment> callback) {
        if (query == null) {
            query = new HashMap<>();
        }
        CustomService customService = getCustomService(baseUrl, DEFAULT_PAYMENT_CONNECT_TIMEOUT, DEFAULT_PAYMENT_READ_TIMEOUT, DEFAULT_PAYMENT_WRITE_TIMEOUT);
        customService.createPayment(transactionId, ripFirstSlash(uri), paymentData, query).enqueue(callback);
    }

    public void getDirectDiscount(String transactionAmount, String payerEmail, String url, String uri, @NonNull Map<String, String> discountAdditionalInfo, Callback<Discount> callback) {
        if (discountAdditionalInfo == null) {
            discountAdditionalInfo = new HashMap<>();
        }
        CustomService customService = getCustomService(url);
        customService.getDirectDiscount(ripFirstSlash(uri), transactionAmount, payerEmail, discountAdditionalInfo).enqueue(callback);
    }

    public void getCodeDiscount(String discountCode, String transactionAmount, String payerEmail, Context context, String url, String uri, @NonNull Map<String, String> discountAdditionalInfo, Callback<Discount> callback) {
        if (discountAdditionalInfo == null) {
            discountAdditionalInfo = new HashMap<>();
        }
        CustomService customService = getCustomService(url);
        customService.getCodeDiscount(ripFirstSlash(uri), transactionAmount, payerEmail, discountCode, discountAdditionalInfo).enqueue(callback);
    }

    private Retrofit getDefaultRetrofit() {
        return getDefaultRetrofit(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    private Retrofit getDefaultRetrofit(int connectTimeout, int readTimeout, int writeTimeout) {
        String baseUrl;
        if (mServicePreference != null && !TextUtil.isEmpty(mServicePreference.getDefaultBaseURL())) {
            baseUrl = mServicePreference.getDefaultBaseURL();
        } else {
            baseUrl = MP_API_BASE_URL;
        }
        return getRetrofit(baseUrl, connectTimeout, readTimeout, writeTimeout);
    }

    private Retrofit getGatewayRetrofit() {
        return getGatewayRetrofit(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    private Retrofit getGatewayRetrofit(int connectTimeout, int readTimeout, int writeTimeout) {
        String baseUrl;
        if (mServicePreference != null && !TextUtil.isEmpty(mServicePreference.getGatewayBaseURL())) {
            baseUrl = mServicePreference.getGatewayBaseURL();
        } else if (mServicePreference != null && !TextUtil.isEmpty(mServicePreference.getDefaultBaseURL())) {
            baseUrl = mServicePreference.getDefaultBaseURL();
        } else {
            baseUrl = MP_API_BASE_URL;
        }
        return getRetrofit(baseUrl, connectTimeout, readTimeout, writeTimeout);
    }

    private CustomService getCustomService(String url) {
        return getCustomService(url, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    private CustomService getCustomService(String baseUrl, int connectTimeout, int readTimeout, int writeTimeout) {
        Retrofit retrofit = getRetrofit(baseUrl, connectTimeout, readTimeout, writeTimeout);
        return retrofit.create(CustomService.class);
    }


    private static String ripFirstSlash(String uri) {
        return uri.startsWith("/") ? uri.substring(1) : uri;
    }

    private Retrofit getRetrofit(String baseUrl, int connectTimeout, int readTimeout, int writeTimeout) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                .client(HttpClientUtil.getClient(this.mContext, connectTimeout, readTimeout, writeTimeout))
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private String getListAsString(List<String> list, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list != null) {
            for (String typeId : list) {
                stringBuilder.append(typeId);
                if (!typeId.equals(list.get(list.size() - 1))) {
                    stringBuilder.append(separator);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static class Builder {

        private Context mContext;
        private String mPublicKey;
        public String mPrivateKey;
        public ServicePreference mServicePreference;
        private Boolean mBetaEnvironment;

        public Builder() {

            mContext = null;
            mPublicKey = null;
        }

        public Builder setContext(Context context) {

            if (context == null) throw new IllegalArgumentException("context is null");
            this.mContext = context;
            return this;
        }

        public Builder setPrivateKey(String key) {

            this.mPrivateKey = key;
            return this;
        }

        public Builder setPublicKey(String key) {

            this.mPublicKey = key;
            return this;
        }

        public Builder setServicePreference(ServicePreference servicePreference) {

            this.mServicePreference = servicePreference;
            return this;
        }

        public Builder setBetaEnvironment(Boolean betaEnvironment) {

            this.mBetaEnvironment = betaEnvironment;
            return this;
        }

        public MercadoPagoServices build() {

            if (this.mContext == null) throw new IllegalStateException("context is null");
            if (TextUtil.isEmpty(this.mPublicKey) && TextUtil.isEmpty(this.mPrivateKey))
                throw new IllegalStateException("key is null");

            return new MercadoPagoServices(this);
        }
    }
}
