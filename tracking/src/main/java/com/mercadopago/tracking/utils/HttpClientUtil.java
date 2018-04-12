package com.mercadopago.tracking.utils;

import android.content.Context;

import com.mercadopago.tracking.core.Settings;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by vaserber on 6/5/17.
 */

public class HttpClientUtil {

    private static OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public synchronized static OkHttpClient getClient(Context context) {
        if (client == null) {

            // Set log info
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(Settings.OKHTTP_LOGGING);

            // Set cache size
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            Cache cache = new Cache(new File(context.getCacheDir().getPath() + "okhttp"), cacheSize);

            // Set client
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(cache)
                    .addInterceptor(interceptor);

            client = okHttpClientBuilder.build();
        }
        return client;
    }
}
