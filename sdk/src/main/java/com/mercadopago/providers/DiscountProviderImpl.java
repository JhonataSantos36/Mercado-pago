package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.lite.controllers.CustomServicesHandler;
import com.mercadopago.core.CustomServer;
import com.mercadopago.core.MercadoPagoServicesAdapter;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.mvp.TaggedCallback;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.TextUtil;

public class DiscountProviderImpl implements DiscountsProvider {

    //Errors
    private static final String DISCOUNT_ERROR_AMOUNT_DOESNT_MATCH = "amount-doesnt-match";
    private static final String DISCOUNT_ERROR_RUN_OUT_OF_USES = "run out of uses";
    private static final String DISCOUNT_ERROR_CAMPAIGN_DOESNT_MATCH = "campaign-doesnt-match";
    private static final String DISCOUNT_ERROR_CAMPAIGN_EXPIRED = "campaign-expired";

    private final MercadoPagoServicesAdapter mercadoPago;

    private final Context context;
    private final String merchantBaseUrl;
    private final String merchantDiscountUrl;
    private final String merchantGetDiscountUri;

    private final ServicePreference servicePreference;

    public DiscountProviderImpl(Context context, String publicKey, String merchantBaseUrl, String merchantDiscountUrl, String merchantGetDiscountUri) {
        this.context = context;
        this.merchantBaseUrl = merchantBaseUrl;
        this.merchantDiscountUrl = merchantDiscountUrl;
        this.merchantGetDiscountUri = merchantGetDiscountUri;
        servicePreference = CustomServicesHandler.getInstance().getServicePreference();
        mercadoPago = new MercadoPagoServicesAdapter(context, publicKey);
    }

    @Override
    public void getDirectDiscount(String amount, String payerEmail, final TaggedCallback<Discount> taggedCallback) {
        if (isMerchantServerDiscountsAvailable()) {
            getMerchantDirectDiscount(amount, payerEmail, taggedCallback);
        } else {
            mercadoPago.getDirectDiscount(amount, payerEmail, taggedCallback);
        }
    }

    private void getMerchantDirectDiscount(String amount, String payerEmail, final TaggedCallback<Discount> taggedCallback) {
        CustomServer.getDirectDiscount(context, amount, payerEmail, servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo(), new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                taggedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                taggedCallback.onFailure(new MercadoPagoError(apiException, ApiUtil.RequestOrigin.GET_DIRECT_DISCOUNT));
            }
        });
    }

    @Override
    public void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode, final TaggedCallback<Discount> taggedCallback) {
        if (isMerchantServerDiscountsAvailable()) {
            CustomServer.getCodeDiscount(discountCode, transactionAmount, payerEmail, context, servicePreference.getGetMerchantDiscountBaseURL(), servicePreference.getGetMerchantDiscountURI(), servicePreference.getGetDiscountAdditionalInfo(), taggedCallback);
        } else {
            mercadoPago.getCodeDiscount(transactionAmount, payerEmail, discountCode, taggedCallback);
        }
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
            merchantBaseUrl = merchantDiscountUrl;
        }

        return merchantBaseUrl;
    }
}
