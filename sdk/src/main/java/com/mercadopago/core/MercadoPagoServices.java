package com.mercadopago.core;

import android.content.Context;

import com.mercadopago.BuildConfig;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.PayerIntent;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.PaymentResult;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SecurityCodeIntent;
import com.mercadopago.model.Token;
import com.mercadopago.mptracker.MPTracker;
import com.mercadopago.services.BankDealService;
import com.mercadopago.services.DiscountService;
import com.mercadopago.services.GatewayService;
import com.mercadopago.services.IdentificationService;
import com.mercadopago.services.PaymentService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoServices {
    public static final String KEY_TYPE_PUBLIC = "public_key";
    public static final String KEY_TYPE_PRIVATE = "private_key";

    public static final int BIN_LENGTH = 6;

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";

    private static final String PAYMENT_RESULT_API_VERSION = "1.3.x";
    private static final String PAYMENT_METHODS_OPTIONS_API_VERSION = "1.3.x";

    private String mKey = null;
    private String mKeyType = null;
    private Context mContext = null;

    Retrofit mRetrofit;

    private MercadoPagoServices(MercadoPagoServices.Builder builder) {

        this.mContext = builder.mContext;
        this.mKey = builder.mKey;
        this.mKeyType = builder.mKeyType;

        System.setProperty("http.keepAlive", "false");

        mRetrofit = new Retrofit.Builder()
                .baseUrl(MP_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                .client(HttpClientUtil.getClient(this.mContext, 10, 20, 20))
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    public void getPreference(String checkoutPreferenceId, Callback<CheckoutPreference> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_PREFERENCE", "3", mKey, BuildConfig.VERSION_NAME, mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPreference(checkoutPreferenceId, this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createPayment(final PaymentBody paymentBody, final Callback<Payment> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "CREATE_PAYMENT", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            Retrofit paymentsRetrofitAdapter = new Retrofit.Builder()
                    .baseUrl(MP_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                    .client(HttpClientUtil.getClient(this.mContext, 10, 40, 40))
                    .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                    .build();

            PaymentService service = paymentsRetrofitAdapter.create(PaymentService.class);
            service.createPayment(paymentBody.getTransactionId(), paymentBody).enqueue(callback);

        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "CREATE_SAVED_CARD_TOKEN", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    savedCardToken.setDevice(mContext);
                    GatewayService service = mRetrofit.create(GatewayService.class);
                    service.getToken(mKey, savedCardToken).enqueue(callback);
                }
            }).start();

        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "CREATE_CARD_TOKEN", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    cardToken.setDevice(mContext);
                    GatewayService service = mRetrofit.create(GatewayService.class);
                    service.getToken(mKey, cardToken).enqueue(callback);
                }
            }).start();
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void cloneToken(final String tokenId, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "CLONE_TOKEN", "1", mKey, BuildConfig.VERSION_NAME, mContext);

            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(tokenId, this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void putSecurityCode(final String tokenId, final SecurityCodeIntent securityCodeIntent, final Callback<Token> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "CLONE_TOKEN", "1", mKey, BuildConfig.VERSION_NAME, mContext);

            GatewayService service = mRetrofit.create(GatewayService.class);
            service.getToken(tokenId, this.mKey, securityCodeIntent).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_BANK_DEALS", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            BankDealService service = mRetrofit.create(BankDealService.class);
            service.getBankDeals(this.mKey, mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }


    public void getIdentificationTypes(Callback<List<IdentificationType>> callback) {
        IdentificationService service = mRetrofit.create(IdentificationService.class);
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_IDENTIFICATION_TYPES", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            service.getIdentificationTypes(this.mKey, null).enqueue(callback);
        } else {
            service.getIdentificationTypes(null, this.mKey).enqueue(callback);
        }
    }

    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, Callback<List<Installment>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_INSTALLMENTS", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getInstallments(this.mKey, bin, amount, issuerId, paymentMethodId,
                    mContext.getResources().getConfiguration().locale.toString()).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getIssuers(String paymentMethodId, String bin, final Callback<List<Issuer>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_ISSUERS", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getIssuers(this.mKey, paymentMethodId, bin).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_PAYMENT_METHODS", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPaymentMethods(this.mKey).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, final Callback<PaymentMethodSearch> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_PAYMENT_METHOD_SEARCH", "1", mKey, BuildConfig.VERSION_NAME, mContext);
            PayerIntent payerIntent = new PayerIntent(payer);
            PaymentService service = mRetrofit.create(PaymentService.class);
            String separator = ",";
            String excludedPaymentTypesAppended = getListAsString(excludedPaymentTypes, separator);
            String excludedPaymentMethodsAppended = getListAsString(excludedPaymentMethods, separator);

            service.getPaymentMethodSearch(Locale.getDefault().getLanguage(), this.mKey, amount, excludedPaymentTypesAppended, excludedPaymentMethodsAppended, payerIntent, PAYMENT_METHODS_OPTIONS_API_VERSION).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getDirectDiscount(String amount, String payerEmail, final Callback<Discount> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_DIRECT_DISCOUNT", "1", mKey, BuildConfig.VERSION_NAME, mContext);

            DiscountService service = mRetrofit.create(DiscountService.class);
            service.getDirectDiscount(this.mKey, amount, payerEmail).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public void getPaymentResult(Long paymentId, String paymentTypeId, final Callback<PaymentResult> callback) {
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            MPTracker.getInstance().trackEvent("NO_SCREEN", "GET_INSTRUCTIONS", "1", mKey, BuildConfig.VERSION_NAME, mContext);

            PaymentService service = mRetrofit.create(PaymentService.class);
            service.getPaymentResult(Locale.getDefault().getLanguage(), paymentId, this.mKey, paymentTypeId, PAYMENT_RESULT_API_VERSION).enqueue(callback);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(String bin, List<PaymentMethod> paymentMethods) {
        if (bin.length() == BIN_LENGTH) {
            List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        } else
            throw new RuntimeException("Invalid bin: " + BIN_LENGTH + " digits needed, " + bin.length() + " found");
    }
    public static class Builder {

        private Context mContext;
        private String mKey;
        private String mKeyType;

        public Builder() {

            mContext = null;
            mKey = null;
        }

        public MercadoPagoServices.Builder setContext(Context context) {

            if (context == null) throw new IllegalArgumentException("context is null");
            this.mContext = context;
            return this;
        }

        public MercadoPagoServices.Builder setKey(String key, String keyType) {

            this.mKey = key;
            this.mKeyType = keyType;
            return this;
        }

        public MercadoPagoServices.Builder setPrivateKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PRIVATE;
            return this;
        }

        public MercadoPagoServices.Builder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
            return this;
        }

        public MercadoPagoServices build() {

            if (this.mContext == null) throw new IllegalStateException("context is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if ((!this.mKeyType.equals(MercadoPago.KEY_TYPE_PRIVATE)) &&
                    (!this.mKeyType.equals(MercadoPago.KEY_TYPE_PUBLIC)))
                throw new IllegalArgumentException("invalid key type");
            return new MercadoPagoServices(this);
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
}
