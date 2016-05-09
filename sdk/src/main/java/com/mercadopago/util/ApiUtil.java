package com.mercadopago.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mercadopago.R;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;

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
                mpException = new MPException(errorMessage, true);
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
}
