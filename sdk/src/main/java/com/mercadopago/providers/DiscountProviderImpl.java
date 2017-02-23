package com.mercadopago.providers;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.core.MercadoPagoServices;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Discount;
import com.mercadopago.mvp.OnResourcesRetrievedCallback;

/**
 * Created by mromar on 1/24/17.
 */

public class DiscountProviderImpl implements DiscountsProvider {

    //Errors
    private static final String DISCOUNT_ERROR_AMOUNT_DOESNT_MATCH = "amount-doesnt-match";
    private static final String DISCOUNT_ERROR_RUN_OUT_OF_USES = "run out of uses";
    private static final String DISCOUNT_ERROR_CAMPAIGN_DOESNT_MATCH = "campaign-doesnt-match";
    private static final String DISCOUNT_ERROR_CAMPAIGN_EXPIRED = "campaign-expired";

    private final MercadoPagoServices mercadoPago;

    private Context context;

    public DiscountProviderImpl(Context context, String publicKey) {
        this.context = context;
        if (publicKey == null) throw new IllegalStateException("public key not found");
        if (context == null) throw new IllegalStateException("context not found");

        mercadoPago = new MercadoPagoServices.Builder()
                .setContext(context)
                .setPublicKey(publicKey)
                .build();
    }

    @Override
    public void getDirectDiscount(String transactionAmount, String payerEmail, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        mercadoPago.getDirectDiscount(transactionAmount, payerEmail, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    @Override
    public void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode, final OnResourcesRetrievedCallback<Discount> onResourcesRetrievedCallback) {
        mercadoPago.getCodeDiscount(transactionAmount, payerEmail, discountCode, new Callback<Discount>() {
            @Override
            public void success(Discount discount) {
                onResourcesRetrievedCallback.onSuccess(discount);
            }

            @Override
            public void failure(ApiException apiException) {
                onResourcesRetrievedCallback.onFailure(new MercadoPagoError(apiException));
            }
        });
    }

    @Override
    public String getApiErrorMessage(String error) {
        String message;
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
        return message;
    }

    @Override
    public String getStandardErrorMessage() {
        return context.getString(R.string.mpsdk_standard_error_message);
    }
}
