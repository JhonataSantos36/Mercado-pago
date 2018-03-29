package com.mercadopago.exceptions;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.lite.exceptions.CardTokenException;
import com.mercadopago.lite.exceptions.CheckoutPreferenceException;

public class ExceptionHandler {

    public static String getErrorMessage(Context context, CheckoutPreferenceException exception) {
        String errorMessage;

        switch (exception.getErrorCode()) {
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

            case CheckoutPreferenceException.EXCLUDED_ALL_PAYMENT_TYPES:
                errorMessage = context.getString(R.string.mpsdk_error_message_excluded_all_payment_type);
                break;
            case CheckoutPreferenceException.NO_EMAIL_FOUND:
                errorMessage = context.getString(R.string.mpsdk_error_message_email_required);
                break;
            default:
                errorMessage = "";
        }
        return errorMessage;
    }

    public static String getErrorMessage(Context context, CardTokenException exception) {
        String errorMessage;

        switch (exception.getErrorCode()) {
            case CardTokenException.INVALID_EMPTY_CARD:
                errorMessage = context.getString(R.string.mpsdk_invalid_empty_card);
                break;

            case CardTokenException.INVALID_CARD_BIN:
                errorMessage = context.getString(R.string.mpsdk_invalid_card_bin);
                break;

            case CardTokenException.INVALID_CARD_LENGTH:
                errorMessage = context.getString(R.string.mpsdk_invalid_card_length, exception.getExtraParams());
                break;

            case CardTokenException.INVALID_CARD_LUHN:
                errorMessage = context.getString(R.string.mpsdk_invalid_card_luhn);
                break;

            case CardTokenException.INVALID_CVV_LENGTH:
                errorMessage = context.getString(R.string.mpsdk_invalid_cvv_length, exception.getExtraParams());
                break;

            case CardTokenException.INVALID_FIELD:
                errorMessage = context.getString(R.string.mpsdk_invalid_field);
                break;

            case CardTokenException.INVALID_CARD_NUMBER_INCOMPLETE:
                errorMessage = context.getString(R.string.mpsdk_invalid_card_number_incomplete);
                break;

            case CardTokenException.INVALID_PAYMENT_METHOD:
                errorMessage = context.getString(R.string.mpsdk_invalid_payment_method);
                break;
            default:
                errorMessage = "";
        }
        return errorMessage;
    }
}
