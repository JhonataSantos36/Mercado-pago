package com.mercadopago.testlib;

import android.support.test.InstrumentationRegistry;
import java.io.InputStream;

public final class MockTestUtils {

    private MockTestUtils() {
    }

    public static String getBody(final int rawId) {
        String body = "";
        final InputStream inputStream = InstrumentationRegistry.getContext().getResources().openRawResource(rawId);
        try {
            final byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            body = new String(b);
        } catch (final Exception ignored) {
        }
        return body;
    }
}
