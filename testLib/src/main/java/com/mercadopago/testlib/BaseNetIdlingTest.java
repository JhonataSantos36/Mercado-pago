package com.mercadopago.testlib;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import okhttp3.OkHttpClient;

@RunWith(AndroidJUnit4.class)
public abstract class BaseNetIdlingTest {

    private static final String RES_NAME_OK_HTTP = "OK-HTTP";
    private OkHttp3IdlingResource okHttp3IdlingResource;

    @Before
    public void setUp() {
        okHttp3IdlingResource = OkHttp3IdlingResource.create(RES_NAME_OK_HTTP, getClient());
        IdlingRegistry.getInstance().register(okHttp3IdlingResource);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource);
    }

    protected abstract OkHttpClient getClient();
}
