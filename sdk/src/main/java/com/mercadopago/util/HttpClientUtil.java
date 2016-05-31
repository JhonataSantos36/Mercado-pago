package com.mercadopago.util;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

import retrofit.client.Client;
import retrofit.client.OkClient;

public class HttpClientUtil {

    private static Client client;

    protected HttpClientUtil() {}

    public static synchronized Client getClient(Context context) {

        if (client == null) {
            Log.i("HttpClientUtil", "new instance");
            OkHttpClient okHttpClient = new OkHttpClient();

            okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(20, TimeUnit.SECONDS);



            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(new File(context.getCacheDir().getPath() + "okhttp"), cacheSize);
            okHttpClient.setCache(cache);
            client = new OkClient(okHttpClient);
        }
        return client;
    }

    public static synchronized Client getPaymentClient(Context context) {

        if (client == null) {
            Log.i("HttpClientUtil", "new instance");
            OkHttpClient okHttpClient = new OkHttpClient();

            okHttpClient.setConnectTimeout(40, TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(40, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(40, TimeUnit.SECONDS);

            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(new File(context.getCacheDir().getPath() + "okhttp"), cacheSize);
            okHttpClient.setCache(cache);
            client = new OkClient(okHttpClient);
        }
        return client;
    }

    public static void bindClient(Client client) {
        HttpClientUtil.client = client;
    }

    public static void unbindClient() {
        client = null;
    }
}
