package com.mercadopago.util;

import android.app.Activity;
import android.content.Intent;

import com.mercadopago.ErrorActivity;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;

/**
 * Created by mreverter on 9/5/16.
 */
public class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 12;

    public static void startErrorActivity(Activity launcherActivity, MPException mpException) {
        Intent intent = new Intent(launcherActivity, ErrorActivity.class);
        intent.putExtra("mpException", mpException);
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }
}
