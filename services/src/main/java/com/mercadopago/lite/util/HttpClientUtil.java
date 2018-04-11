package com.mercadopago.lite.util;

import android.content.Context;

import com.mercadopago.lite.core.Settings;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpClientUtil {

    private static OkHttpClient client;
    private static OkHttpClient customClient;

    public synchronized static OkHttpClient getClient(Context context, int connectTimeout, int readTimeout, int writeTimeout) {

        if (customClientSet()) {
            return customClient;
        } else {
            if (client == null) {
                createClient(context, connectTimeout, readTimeout, writeTimeout);
            }
            return client;
        }
    }

    private static void createClient(Context context, int connectTimeout, int readTimeout, int writeTimeout) {
        // Set log info
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(Settings.OKHTTP_LOGGING);

        // Set cache size
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        okhttp3.Cache cache = new okhttp3.Cache(new File(context.getCacheDir().getPath() + "okhttp"), cacheSize);

        // Set client
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(interceptor);

        client = okHttpClientBuilder.build();
    }

    public static void setCustomClient(OkHttpClient client) {
        customClient = client;
    }

    public static void removeCustomClient() {
        customClient = null;
    }

    private static boolean customClientSet() {
        return customClient != null;
    }
}
