package com.mercadopago.exceptions;

import android.content.Context;

import com.mercadopago.R;

/**
 * Created by mromar on 3/2/16.
 */
public class ExceptionHandler {

    public static String getErrorMessage(Context context, CheckoutPreferenceException mpException) {
        String errorMessage = "";
        switch (mpException.getErrorCode()) {
            case CheckoutPreferenceException.INVALID_ITEM:
                errorMessage = context.getString(R.string.mpsdk_error_message_invalid_item);
                break;

            case CheckoutPreferenceException.EXPIRED_PREFERENCE:
                errorMessage = context.getString(R.string.mpsdk_error_message_expired_preference);
                break;

            case CheckoutPreferenceException.INACTIVE_PREFERENCE:
                errorMessage = context.getString(R.string.mpsdk_error_message_inactive_preference);
                break;

            case CheckoutPreferenceException.INVALID_INSTALLMENTS:
                errorMessage = context.getString(R.string.mpsdk_error_message_invalid_installments);
                break;

            case CheckoutPreferenceException.EXCLUDED_ALL_PAYMENTTYPES:
                errorMessage = context.getString(R.string.mpsdk_error_message_excluded_all_payment_type);
                break;
            }
        return errorMessage;
    }
}
