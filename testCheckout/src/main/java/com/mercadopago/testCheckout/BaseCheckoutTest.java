package com.mercadopago.testCheckout;


import android.support.test.InstrumentationRegistry;

import com.mercadopago.testlib.BaseNetIdlingTest;
import com.mercadopago.util.HttpClientUtil;

import okhttp3.OkHttpClient;

public abstract class BaseCheckoutTest extends BaseNetIdlingTest {

    @Override
    protected OkHttpClient getClient() {
        return HttpClientUtil.getClient(InstrumentationRegistry.getContext(), 10, 10, 10);
    }
}
