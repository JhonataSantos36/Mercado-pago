package com.mercadopago.testcheckout;


import android.support.test.InstrumentationRegistry;

import com.mercadopago.testlib.HttpResource;
import com.mercadopago.util.HttpClientUtil;

import okhttp3.OkHttpClient;

public class CheckoutResource extends HttpResource {
    @Override
    protected OkHttpClient getClient() {
        return HttpClientUtil.getClient(InstrumentationRegistry.getContext(), 10, 10, 10);
    }
}
