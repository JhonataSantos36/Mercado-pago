package com.mercadopago.tracking.mocks;

import android.content.Context;

import java.io.InputStream;

/**
 * Created by marlanti on 8/7/17.
 */

public class MockUtils {

    public static String getFile(Context context, String fileName) {

        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer);

        } catch (Exception e) {

            return "";
        }
    }
}
