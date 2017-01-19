package com.mercadopago.util;

import android.app.Activity;
import android.content.Intent;

import com.mercadopago.ErrorActivity;
import com.mercadopago.exceptions.MercadoPagoError;

/**
 * Created by mreverter on 9/5/16.
 */
public class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 94;

    public static void startErrorActivity(Activity launcherActivity, String message, boolean recoverable) {
        MercadoPagoError mercadoPagoError = new MercadoPagoError(message, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError);
    }

    public static void startErrorActivity(Activity launcherActivity, String message, String errorDetail, boolean recoverable) {
        MercadoPagoError mercadoPagoError = new MercadoPagoError(message, errorDetail, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError);
    }

    public static void startErrorActivity(Activity launcherActivity, MercadoPagoError mercadoPagoError) {
        Intent intent = new Intent(launcherActivity, ErrorActivity.class);
        intent.putExtra("mercadoPagoError", JsonUtil.getInstance().toJson(mercadoPagoError));
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }
}
