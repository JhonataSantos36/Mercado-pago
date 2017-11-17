package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.controllers.CustomServicesHandler;
import com.mercadopago.core.CustomServer;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Campaign;
import com.mercadopago.model.Discount;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;

import java.util.List;

import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.TextUtil;

import java.util.Map;

/**
 * Created by mromar on 1/24/17.
 */

public class DiscountProviderImpl implements DiscountsProvider {

    //Errors
    private static final String DISCOUNT_ERROR_AMOUNT_DOESNT_MATCH = "amount-doesnt-match";
    private static final String DISCOUNT_ERROR_RUN_OUT_OF_USES = "run out of uses";
    private static final String DISCOUNT_ERROR_CAMPAIGN_DOESNT_MATCH = "campaign-doesnt-match";
    private static final String DISCOUNT_ERROR_CAMPAIGN_EXPIRED = "campaign-expired";

    private final MercadoPagoServicesAdapter mercadoPago;

    private Context context;
    private String merchantBaseUrl;
    private String merchantDiscountUrl;
    private String merchantGetDiscountUri;
    private Map<String, String> discountAdditionalInfo;

    private ServicePreference servicePreference;

    public DiscountProviderImpl(Context context, String publicKey, String merchantBaseUrl, String merchantDiscountUrl, String merchantGetDiscountUri, Map<String, String> discountAdditionalInfo) {
        this.context = context;
        this.merchantBaseUrl = merchantBaseUrl;
        this.merchantDiscountUrl = merchantDiscountUrl;
        this.merchantGetDiscountUri = merchantGetDiscountUri;
        this.discountAdditionalInfo = discountAdditionalInfo;
        this.servicePreference = CustomServicesHandler.getInstance().getServicePreference();

        if (publicKey == null) throw new IllegalStateException("public key not found");
        if (context == null) throw new IllegalStateException("context not found");

        mercadoPago = new MercadoPagoServicesAdapter.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .build();
    }

    @Override
    public void getDirectDiscount(String amount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        if (isMerchantServerDiscountsAvailable()) {
            getMerchantDirectDiscount(amount, payerEmail, onResourcesRetrievedCallback);
        } else {
            getMPDirectDiscount(amount, payerEmail, onResourcesRetrievedCallback);
        }
    }

    private void getMPDirectDiscount(String amount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        mercadoPago.getDirectDiscount(amount, payerEmail, new Callback<Discount>() {
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

    private void getMerchantDirectDiscount(String amount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        CustomServer.getDirectDiscount(context, amount, payerEmail, servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo(), new Callback<Discount>() {
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
    public void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        if (isMerchantServerDiscountsAvailable()) {
            getMerchantCodeDiscount(transactionAmount, payerEmail, discountCode, onResourcesRetrievedCallback);
        } else {
            getMPCodeDiscount(transactionAmount, payerEmail, discountCode, onResourcesRetrievedCallback);
        }
    }

    private void getMPCodeDiscount(String transactionAmount, String payerEmail, String discountCode, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        mercadoPago.getCodeDiscount(transactionAmount, payerEmail, discountCode, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_CODE_DISCOUNT));
            }
        });
    }

    @Override
    public void getCampaigns(final OnResourcesRetrievedCallback<List<Campaign>> onResourcesRetrievedCallback) {
        mercadoPago.getCampaigns(new Callback<List<Campaign>>() {
            @Override
            public void success(List<Campaign> campaigns) {
                onResourcesRetrievedCallback.onSuccess(campaigns);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_CAMPAIGNS));
            }
        });
    }

    private void getMerchantCodeDiscount(String transactionAmount, String payerEmail, String discountCode, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        CustomServer.getCodeDiscount(discountCode, transactionAmount, payerEmail, context, servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo(), new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_CODE_DISCOUNT));
            }
        });
    }

    @Override
    public String getApiErrorMessage(String error) {
        String message;
        if (error == null) {
            message = context.getString(R.string.mpsdk_something_went_wrong);
        } else {
            if (error.equals(DISCOUNT_ERROR_CAMPAIGN_DOESNT_MATCH)) {
                message = context.getString(R.string.mpsdk_merchant_without_discount_available);
            } else if (error.equals(DISCOUNT_ERROR_RUN_OUT_OF_USES)) {
                message = context.getString(R.string.mpsdk_ran_out_of_quantity_uses_quantity);
            } else if (error.equals(DISCOUNT_ERROR_AMOUNT_DOESNT_MATCH)) {
                message = context.getString(R.string.mpsdk_amount_doesnt_match);
            } else if (error.equals(DISCOUNT_ERROR_CAMPAIGN_EXPIRED)) {
                message = context.getString(R.string.mpsdk_campaign_expired);
            } else {
                message = context.getString(R.string.mpsdk_invalid_code);
            }
        }
        return message;
    }

    @Override
    public String getStandardErrorMessage() {
        return context.getString(R.string.mpsdk_standard_error_message);
    }

    private boolean isMerchantServerDiscountsAvailable() {
        return !TextUtil.isEmpty(getMerchantServerDiscountUrl()) && !TextUtil.isEmpty(merchantGetDiscountUri);
    }

    private String getMerchantServerDiscountUrl() {
        String merchantBaseUrl;

        if (TextUtil.isEmpty(merchantDiscountUrl)) {
            merchantBaseUrl = this.merchantBaseUrl;
        } else {
            merchantBaseUrl = this.merchantDiscountUrl;
        }

        return merchantBaseUrl;
    }
}
