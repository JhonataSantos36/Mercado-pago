package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.BuildConfig;
import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.Discount;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;
import com.mercadopago.tracker.MPTrackingContext;
import com.mercadopago.util.ApiUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by vaserber on 8/24/17.
 */

public class GuessingCardProviderImpl implements GuessingCardProvider {

    private final Context context;
    private final MercadoPagoServicesAdapter mercadoPago;
    private final String publicKey;
    private MPTrackingContext trackingContext;

    public GuessingCardProviderImpl(Context context, String publicKey, String privateKey) {
        this.context = context;
        this.publicKey = publicKey;
        mercadoPago = new MercadoPagoServicesAdapter.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .setPrivateKey(privateKey)
                .build();
    }

    @Override
    public MPTrackingContext getTrackingContext() {
        if (trackingContext == null) {
            trackingContext = new MPTrackingContext.Builder(context, publicKey)
                    .setVersion(BuildConfig.VERSION_NAME)
                    .build();
        }
        return trackingContext;
    }

    @Override
    public void getPaymentMethodsAsync(final OnResourcesRetrievedCallback<List<PaymentMethod>> onResourcesRetrievedCallback) {
        mercadoPago.getPaymentMethods(new Callback<List<PaymentMethod>>() {
            @Override
            public void success(List<PaymentMethod> paymentMethods) {
                onResourcesRetrievedCallback.onSuccess(paymentMethods);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_PAYMENT_METHODS));
            }
        });
    }

    @Override
    public void createTokenAsync(CardToken cardToken, final OnResourcesRetrievedCallback<Token> onResourcesRetrievedCallback) {
        mercadoPago.createToken(cardToken, new Callback<Token>() {
            @Override
            public void success(Token token) {
                onResourcesRetrievedCallback.onSuccess(token);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.CREATE_TOKEN));
            }
        });
    }

    @Override
    public void getIssuersAsync(String paymentMethodId, String bin, final OnResourcesRetrievedCallback<List<Issuer>> onResourcesRetrievedCallback) {
        mercadoPago.getIssuers(paymentMethodId, bin, new Callback<List<Issuer>>() {
            @Override
            public void success(List<Issuer> issuers) {
                onResourcesRetrievedCallback.onSuccess(issuers);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_ISSUERS));
            }
        });
    }

    @Override
    public void getInstallmentsAsync(String bin, BigDecimal amount, Long issuerId, String paymentMethodId, final OnResourcesRetrievedCallback<List<Installment>> onResourcesRetrievedCallback) {
        mercadoPago.getInstallments(bin, amount, issuerId, paymentMethodId, new Callback<List<Installment>>() {
            @Override
            public void success(List<Installment> installments) {
                onResourcesRetrievedCallback.onSuccess(installments);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_INSTALLMENTS));
            }
        });
    }

    @Override
    public void getIdentificationTypesAsync(final OnResourcesRetrievedCallback<List<IdentificationType>> onResourcesRetrievedCallback) {
        mercadoPago.getIdentificationTypes(new Callback<List<IdentificationType>>() {
            @Override
            public void success(List<IdentificationType> identificationTypes) {
                onResourcesRetrievedCallback.onSuccess(identificationTypes);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES));
            }
        });
    }

    @Override
    public void getBankDealsAsync(final OnResourcesRetrievedCallback<List<BankDeal>> onResourcesRetrievedCallback) {
        mercadoPago.getBankDeals(new Callback<List<BankDeal>>() {
            @Override
            public void success(List<BankDeal> bankDeals) {
                onResourcesRetrievedCallback.onSuccess(bankDeals);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_BANK_DEALS));
            }
        });
    }

    @Override
    public void getMPDirectDiscount(String transactionAmount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        mercadoPago.getDirectDiscount(transactionAmount, payerEmail, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_DIRECT_DISCOUNT));
            }
        });
    }

    @Override
    public void getDirectDiscountAsync(String transactionAmount, String payerEmail, String merchantDiscountUrl, String merchantDiscountUri, Map<String, String> discountAdditionalInfo, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        MerchantServer.getDirectDiscount(transactionAmount, payerEmail, context, merchantDiscountUrl, merchantDiscountUri, discountAdditionalInfo, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_MERCHANT_DIRECT_DISCOUNT));
            }
        });
    }

    @Override
    public String getMissingInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_installment_for_issuer);
    }

    @Override
    public String getMultipleInstallmentsForIssuerErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_multiple_installments_for_issuer);
    }

    @Override
    public String getMissingPayerCostsErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_payer_cost);
    }

    @Override
    public String getMissingIdentificationTypesErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_identification_types);
    }

    @Override
    public String getMissingPublicKeyErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_public_key);
    }

    @Override
    public String getInvalidIdentificationNumberErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_identification_number);
    }

    @Override
    public String getInvalidExpiryDateErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_expiry_date);
    }

    @Override
    public String getInvalidEmptyNameErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_empty_name);
    }

    @Override
    public String getSettingNotFoundForBinErrorMessage() {
        return context.getString(R.string.mpsdk_error_message_missing_setting_for_bin);
    }

    @Override
    public String getInvalidFieldErrorMessage() {
        return context.getString(R.string.mpsdk_invalid_field);
    }

}
