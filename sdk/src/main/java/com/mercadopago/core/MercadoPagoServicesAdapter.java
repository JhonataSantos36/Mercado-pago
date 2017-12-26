package com.mercadopago.core;

import android.content.Context;
import android.os.Build;

import com.mercadopago.callbacks.Callback;

import com.mercadopago.controllers.CustomServicesHandler;
import com.mercadopago.lite.core.MercadoPagoServices;
import com.mercadopago.model.Installment;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Campaign;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Instructions;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Site;
import com.mercadopago.model.Token;
import com.mercadopago.model.SecurityCodeIntent;
import com.mercadopago.model.PaymentBody;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.TextUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mreverter on 1/17/17.
 */

public class MercadoPagoServicesAdapter {
    public static final int BIN_LENGTH = 6;

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";
    private static final String MP_CHECKOUT_PAYMENTS_URI = "/v1/checkout/payments";

    private static final String PAYMENT_RESULT_API_VERSION = "1.4";
    private static final String PAYMENT_METHODS_OPTIONS_API_VERSION = "1.4";

    public static final int DEFAULT_CONNECT_TIMEOUT = 10;
    public static final int DEFAULT_READ_TIMEOUT = 20;
    public static final int DEFAULT_WRITE_TIMEOUT = 20;

    public static final int DEFAULT_PAYMENT_CONNECT_TIMEOUT = 10;
    public static final int DEFAULT_PAYMENT_READ_TIMEOUT = 20;
    public static final int DEFAULT_PAYMENT_WRITE_TIMEOUT = 20;

    private final MercadoPagoServices mMercadoPagoServices;

    private MercadoPagoServicesAdapter(MercadoPagoServicesAdapter.Builder builder) {

        ServicePreference servicePreference = CustomServicesHandler.getInstance().getServicePreference();
        ModelsAdapter.adapt(servicePreference);

        mMercadoPagoServices = new MercadoPagoServices.Builder()
                .setContext(builder.mContext)
                .setPublicKey(builder.mPublicKey)
                .setPrivateKey(builder.mPrivateKey)
                .build();

        disableConnectionReuseIfNecessary();
    }

    public void getPreference(String checkoutPreferenceId, final Callback<CheckoutPreference> callback) {
        mMercadoPagoServices.getCheckoutPreference(checkoutPreferenceId, new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.preferences.CheckoutPreference>() {
            @Override
            public void success(com.mercadopago.lite.preferences.CheckoutPreference checkoutPreference) {
                callback.success(ModelsAdapter.adapt(checkoutPreference));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getInstructions(Long paymentId, String paymentTypeId, final Callback<Instructions> callback) {
        mMercadoPagoServices.getInstructions(paymentId, paymentTypeId, new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Instructions>() {
            @Override
            public void success(com.mercadopago.lite.model.Instructions instructions) {
                callback.success(ModelsAdapter.adapt(instructions));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getPaymentMethodSearch(BigDecimal amount, List<String> excludedPaymentTypes, List<String> excludedPaymentMethods, Payer payer, Site site, final Callback<PaymentMethodSearch> callback) {
        mMercadoPagoServices.getPaymentMethodSearch(amount, excludedPaymentTypes, excludedPaymentMethods, ModelsAdapter.adapt(payer), ModelsAdapter.adapt(site), new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.PaymentMethodSearch>() {
            @Override
            public void success(com.mercadopago.lite.model.PaymentMethodSearch paymentMethodSearch) {
                callback.success(ModelsAdapter.adapt(paymentMethodSearch));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }


    public void createPayment(final PaymentBody paymentBody, final Callback<Payment> callback) {
        Map<String, Object> adaptedBody = ModelsAdapter.adapt(paymentBody);
        mMercadoPagoServices.createPayment(paymentBody.getTransactionId(), MP_API_BASE_URL, MP_CHECKOUT_PAYMENTS_URI, adaptedBody, new HashMap<String, String>(), new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Payment>() {
            @Override
            public void success(com.mercadopago.lite.model.Payment payment) {
                callback.success(ModelsAdapter.adapt(payment));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }

        });
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        mMercadoPagoServices.createToken(ModelsAdapter.adapt(savedCardToken), new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Token>() {
            @Override
            public void success(com.mercadopago.lite.model.Token token) {
                callback.success(ModelsAdapter.adapt(token));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        mMercadoPagoServices.createToken(ModelsAdapter.adapt(cardToken), new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Token>() {
            @Override
            public void success(com.mercadopago.lite.model.Token token) {
                callback.success(ModelsAdapter.adapt(token));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void createToken(final SavedESCCardToken savedESCCardToken, final Callback<Token> callback) {
        mMercadoPagoServices.createToken(ModelsAdapter.adapt(savedESCCardToken), new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Token>() {
            @Override
            public void success(com.mercadopago.lite.model.Token token) {
                callback.success(ModelsAdapter.adapt(token));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void cloneToken(final String tokenId, final Callback<Token> callback) {
        mMercadoPagoServices.cloneToken(tokenId, new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Token>() {
            @Override
            public void success(com.mercadopago.lite.model.Token token) {
                callback.success(ModelsAdapter.adapt(token));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void putSecurityCode(final String tokenId, final SecurityCodeIntent securityCodeIntent, final Callback<Token> callback) {
        com.mercadopago.lite.model.requests.SecurityCodeIntent securityCodeIntentAdapted = ModelsAdapter.adapt(securityCodeIntent);

        mMercadoPagoServices.putSecurityCode(tokenId, securityCodeIntentAdapted, new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Token>() {
            @Override
            public void success(com.mercadopago.lite.model.Token token) {
                callback.success(ModelsAdapter.adapt(token));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        mMercadoPagoServices.getBankDeals(new com.mercadopago.lite.callbacks.Callback<List<com.mercadopago.lite.model.BankDeal>>() {
            @Override
            public void success(List<com.mercadopago.lite.model.BankDeal> bankDeals) {
                callback.success(ModelsAdapter.adaptBankDeals(bankDeals));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }


    public void getIdentificationTypes(final Callback<List<IdentificationType>> callback) {
        mMercadoPagoServices.getIdentificationTypes(new com.mercadopago.lite.callbacks.Callback<List<com.mercadopago.lite.model.IdentificationType>>() {
            @Override
            public void success(List<com.mercadopago.lite.model.IdentificationType> identificationTypes) {
                callback.success(ModelsAdapter.adaptIdentificationTypes(identificationTypes));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final Callback<List<Installment>> callback) {
        mMercadoPagoServices.getInstallments(bin, amount, issuerId, paymentMethodId, new com.mercadopago.lite.callbacks.Callback<List<com.mercadopago.lite.model.Installment>>() {
            @Override
            public void success(List<com.mercadopago.lite.model.Installment> installments) {
                callback.success(ModelsAdapter.adaptInstallments(installments));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getIssuers(String paymentMethodId, String bin, final Callback<List<Issuer>> callback) {
        mMercadoPagoServices.getIssuers(paymentMethodId, bin, new com.mercadopago.lite.callbacks.Callback<List<com.mercadopago.lite.model.Issuer>>() {
            @Override
            public void success(List<com.mercadopago.lite.model.Issuer> issuers) {
                callback.success(ModelsAdapter.adaptIssuers(issuers));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        mMercadoPagoServices.getPaymentMethods(new com.mercadopago.lite.callbacks.Callback<List<com.mercadopago.lite.model.PaymentMethod>>() {
            @Override
            public void success(List<com.mercadopago.lite.model.PaymentMethod> paymentMethods) {
                callback.success(ModelsAdapter.adaptPaymentMethods(paymentMethods));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getDirectDiscount(String amount, String payerEmail, final Callback<Discount> callback) {
        mMercadoPagoServices.getDirectDiscount(amount, payerEmail, new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Discount>() {
            @Override
            public void success(com.mercadopago.lite.model.Discount discount) {
                callback.success(ModelsAdapter.adapt(discount));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getCodeDiscount(String amount, String payerEmail, String couponCode, final Callback<Discount> callback) {
        mMercadoPagoServices.getCodeDiscount(amount, payerEmail, couponCode, new com.mercadopago.lite.callbacks.Callback<com.mercadopago.lite.model.Discount>() {
            @Override
            public void success(com.mercadopago.lite.model.Discount discount) {
                callback.success(ModelsAdapter.adapt(discount));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    public void getCampaigns(final Callback<List<Campaign>> callback) {
        mMercadoPagoServices.getCampaigns(new com.mercadopago.lite.callbacks.Callback<List<com.mercadopago.lite.model.Campaign>>() {
            @Override
            public void success(List<com.mercadopago.lite.model.Campaign> campaigns) {
                callback.success(ModelsAdapter.adaptCampaigns(campaigns));
            }

            @Override
            public void failure(com.mercadopago.lite.model.ApiException apiException) {
                callback.failure(ModelsAdapter.adapt(apiException));
            }
        });
    }

    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            System.setProperty("http.keepAlive", "false");
        }
    }


    public static class Builder {

        private Context mContext;
        private String mPublicKey;
        public String mPrivateKey;
        public ServicePreference mServicePreference;

        public Builder() {

            mContext = null;
            mPublicKey = null;
        }

        public MercadoPagoServicesAdapter.Builder setContext(Context context) {

            if (context == null) throw new IllegalArgumentException("context is null");
            this.mContext = context;
            return this;
        }

        public MercadoPagoServicesAdapter.Builder setPrivateKey(String key) {

            this.mPrivateKey = key;
            return this;
        }

        public MercadoPagoServicesAdapter.Builder setPublicKey(String key) {

            this.mPublicKey = key;
            return this;
        }

        public MercadoPagoServicesAdapter.Builder setServicePreference(ServicePreference servicePreference) {

            this.mServicePreference = servicePreference;
            return this;
        }

        public MercadoPagoServicesAdapter build() {

            if (this.mContext == null) throw new IllegalStateException("context is null");
            if (TextUtil.isEmpty(this.mPublicKey) && TextUtil.isEmpty(this.mPrivateKey))
                throw new IllegalStateException("key is null");

            return new MercadoPagoServicesAdapter(this);
        }
    }
}
