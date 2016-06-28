package com.mercadopago.util;

import android.app.Activity;
import android.content.Intent;

import com.mercadopago.ErrorActivity;
import com.mercadopago.exceptions.MPException;

/**
 * Created by mreverter on 9/5/16.
 */
public class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 94;

    public static void startErrorActivity(Activity launcherActivity, String message, boolean recoverable) {
        MPException mpException = new MPException(message, recoverable);
        startErrorActivity(launcherActivity, mpException);
    }

    public static void startErrorActivity(Activity launcherActivity, String message, String errorDetail, boolean recoverable) {
        MPException mpException = new MPException(message, errorDetail, recoverable);
        startErrorActivity(launcherActivity, mpException);
    }

    public static void startErrorActivity(Activity launcherActivity, MPException mpException) {
        Intent intent = new Intent(launcherActivity, ErrorActivity.class);
        intent.putExtra("mpException", JsonUtil.getInstance().toJson(mpException));
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }
}
