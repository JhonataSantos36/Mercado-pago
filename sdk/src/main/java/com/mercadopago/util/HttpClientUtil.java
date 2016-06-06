package com.mercadopago.util;

import com.mercadopago.core.Settings;
import java.io.File;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpClientUtil {

    private static okhttp3.OkHttpClient client;
    private static Interceptor mInterceptor;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public synchronized static okhttp3.OkHttpClient getClient(Context context, int connectTimeout, int readTimeout, int writeTimeout) {

        if (client == null) {

            // Set log info
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(Settings.OKHTTP_LOGGING);

            // Set cache size
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            okhttp3.Cache cache = new okhttp3.Cache(new File(context.getCacheDir().getPath() + "okhttp"), cacheSize);

            // Set client
            okhttp3.OkHttpClient.Builder okHttpClientBuilder = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .cache(cache)
                    .addInterceptor(interceptor);
            if(mInterceptor != null) {
                okHttpClientBuilder.addInterceptor(mInterceptor);
            }
            client = okHttpClientBuilder.build();
        }

        return client;
    }

    public static void bindInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
    }

    public static void unbindInterceptor() {
        mInterceptor = null;
    }
}
