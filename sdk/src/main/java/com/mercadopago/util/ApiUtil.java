package com.mercadopago.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mercadopago.ErrorActivity;
import com.mercadopago.R;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Cause;

import retrofit.RetrofitError;

public class ApiUtil {

    public static ApiException getApiException(RetrofitError error) {

        ApiException apiException = null;
        try {
            apiException = (ApiException) error.getBodyAs(ApiException.class);

        } catch (Exception ex) {
            // do nothing
        }

        if (apiException == null) {
            apiException = new ApiException();
            try {
                apiException.setMessage(error.getMessage());
                apiException.setStatus(error.getResponse().getStatus());

            } catch (Exception ex) {
                // do nothing
            }
        }

        return apiException;
    }

    public static void finishWithApiException(Activity activity, RetrofitError error) {

        if (!ApiUtil.checkConnection(activity)) {  // check for connection error

            // Show refresh layout
            LayoutUtil.showRefreshLayout(activity);
            Toast.makeText(activity, activity.getString(R.string.mpsdk_no_connection_message), Toast.LENGTH_LONG).show();

        } else {

            // Return with api exception
            Intent intent = new Intent();
            activity.setResult(activity.RESULT_CANCELED, intent);
            ApiException apiException = getApiException(error);
            intent.putExtra("apiException", apiException);
            activity.finish();
        }
    }

    public static void showApiExceptionError(Activity activity, RetrofitError error) {
        MPException mpException;
        String errorMessage;

        if(!ApiUtil.checkConnection(activity)){
            errorMessage = activity.getString(R.string.mpsdk_no_connection_message);
            mpException = new MPException(errorMessage, true);
        }
        else {
            ApiException apiException = getApiException(error);
            if(apiException != null) {
                mpException = new MPException(apiException);
            }
            else {
                errorMessage = activity.getString(R.string.mpsdk_standard_error_message);
                mpException = new MPException(errorMessage, error.getMessage(), true);
            }
        }

        ErrorUtil.startErrorActivity(activity, mpException);
    }

    public static boolean checkConnection(Context context) {

        if (context != null) {
            try {
                boolean HaveConnectedWifi = false;
                boolean HaveConnectedMobile = false;
                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if ( ni != null && ni.isConnected())
                {
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI)
                        if (ni.isConnectedOrConnecting())
                            HaveConnectedWifi = true;
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
                        if (ni.isConnectedOrConnecting())
                            HaveConnectedMobile = true;
                }

                return HaveConnectedWifi || HaveConnectedMobile;
            }
            catch (Exception ex) {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static String getApiExceptionMessage(Context context, ApiException apiException) {
        String message;

        if(!apiException.getCause().isEmpty()) {
            Cause cause = apiException.getCause().get(0);
            String errorCode = cause.getCode();
            switch (errorCode) {
                case ApiException.ErrorCodes.CUSTOMER_NOT_ALLOWED_TO_OPERATE:
                    message = context.getString(R.string.mpsdk_customer_not_allowed_to_operate);
                    break;
                case ApiException.ErrorCodes.COLLECTOR_NOT_ALLOWED_TO_OPERATE:
                    message = context.getString(R.string.mpsdk_collector_not_allowed_to_operate);
                    break;
                case ApiException.ErrorCodes.INVALID_USERS_INVOLVED:
                    message = context.getString(R.string.mpsdk_invalid_users_involved);
                    break;
                case ApiException.ErrorCodes.CUSTOMER_EQUAL_TO_COLLECTOR:
                    message = context.getString(R.string.mpsdk_customer_equal_to_collector);
                    break;
                case ApiException.ErrorCodes.INVALID_CARD_HOLDER_NAME:
                    message = context.getString(R.string.mpsdk_invalid_card_holder_name);
                    break;
                case ApiException.ErrorCodes.UNAUTHORIZED_CLIENT:
                    message = context.getString(R.string.mpsdk_unauthorized_client);
                    break;
                case ApiException.ErrorCodes.PAYMENT_METHOD_NOT_FOUND:
                    message = context.getString(R.string.mpsdk_payment_method_not_found);
                    break;
                case ApiException.ErrorCodes.INVALID_SECURITY_CODE:
                    message = context.getString(R.string.mpsdk_invalid_security_code);
                    break;
                case ApiException.ErrorCodes.SECURITY_CODE_REQUIRED:
                    message = context.getString(R.string.mpsdk_security_code_required);
                    break;
                case ApiException.ErrorCodes.INVALID_PAYMENT_METHOD:
                    message = context.getString(R.string.mpsdk_invalid_payment_method);
                    break;
                case ApiException.ErrorCodes.INVALID_CARD_NUMBER:
                    message = context.getString(R.string.mpsdk_invalid_card_number);
                    break;
                case ApiException.ErrorCodes.EMPTY_EXPIRATION_MONTH:
                    message = context.getString(R.string.mpsdk_empty_card_expiration_month);
                    break;
                case ApiException.ErrorCodes.EMPTY_EXPIRATION_YEAR:
                    message = context.getString(R.string.mpsdk_empty_card_expiration_year);
                    break;
                case ApiException.ErrorCodes.EMPTY_CARD_HOLDER_NAME:
                    message = context.getString(R.string.mpsdk_empty_card_holder_name);
                    break;
                case ApiException.ErrorCodes.EMPTY_DOCUMENT_NUMBER:
                    message = context.getString(R.string.mpsdk_empty_document_number);
                    break;
                case ApiException.ErrorCodes.EMPTY_DOCUMENT_TYPE:
                    message = context.getString(R.string.mpsdk_empty_document_type);
                    break;
                case ApiException.ErrorCodes.INVALID_PAYMENT_TYPE_ID:
                    message = context.getString(R.string.mpsdk_invalid_payment_type_id);
                    break;
                case ApiException.ErrorCodes.INVALID_PAYMENT_METHOD_ID:
                    message = context.getString(R.string.mpsdk_invalid_payment_method);
                    break;
                case ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_MONTH:
                    message = context.getString(R.string.mpsdk_invalid_card_expiration_month);
                    break;
                case ApiException.ErrorCodes.INVALID_CARD_EXPIRATION_YEAR:
                    message = context.getString(R.string.mpsdk_invalid_card_expiration_year);
                    break;
                case ApiException.ErrorCodes.INVALID_PAYER_EMAIL:
                    message = context.getString(R.string.mpsdk_invalid_payer_email);
                    break;
                default:
                    message = context.getString(R.string.mpsdk_standard_error_message);
                    break;
            }
        }
        else {
            message = context.getString(R.string.mpsdk_standard_error_message);
        }
        return message;
    }
}
