package com.mercadopago.util;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

import retrofit.client.OkClient;

public class HttpClientUtil {

    private static OkHttpClient client;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public synchronized static OkClient getClient(Context context) {

        if (client == null) {

            Log.i("HttpClientUtil", "new instance");
            client = new OkHttpClient();

            client.setConnectTimeout(20, TimeUnit.SECONDS);
            client.setWriteTimeout(20, TimeUnit.SECONDS);
            client.setReadTimeout(20, TimeUnit.SECONDS);

            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(new File(context.getCacheDir().getPath() + "okhttp"), cacheSize);
            client.setCache(cache);
        }

        return new OkClient(client);
    }
}
