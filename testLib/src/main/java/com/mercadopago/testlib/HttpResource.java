package com.mercadopago.testlib;


import android.support.test.espresso.IdlingRegistry;

import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import okhttp3.OkHttpClient;

public abstract class HttpResource implements TestRule {

    private static final String RES_NAME_OK_HTTP = "OK_HTTP";

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                OkHttpClient client = getClient();
                OkHttp3IdlingResource okHttp3IdlingResource = OkHttp3IdlingResource.create(RES_NAME_OK_HTTP, client);
                IdlingRegistry.getInstance().register(okHttp3IdlingResource);
                base.evaluate();
                IdlingRegistry.getInstance().unregister(okHttp3IdlingResource);
            }
        };
    }

    protected abstract OkHttpClient getClient();
}
