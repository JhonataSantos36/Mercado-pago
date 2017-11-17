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
    public static final String ERROR_EXTRA_KEY = "mercadoPagoError";
    public static final String PUBLIC_KEY_EXTRA = "publicKey";

    public static void startErrorActivity(Activity launcherActivity, String message, boolean recoverable, String publicKey) {
        MercadoPagoError mercadoPagoError = new MercadoPagoError(message, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError, publicKey);
    }

    public static void startErrorActivity(Activity launcherActivity, String message, String errorDetail, boolean recoverable, String publicKey) {
        MercadoPagoError mercadoPagoError = new MercadoPagoError(message, errorDetail, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError, publicKey);
    }

    public static void startErrorActivity(Activity launcherActivity, MercadoPagoError mercadoPagoError, String publicKey) {
        Intent intent = new Intent(launcherActivity, ErrorActivity.class);
        intent.putExtra(ERROR_EXTRA_KEY, JsonUtil.getInstance().toJson(mercadoPagoError));
        intent.putExtra(PUBLIC_KEY_EXTRA, publicKey);
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }
}
