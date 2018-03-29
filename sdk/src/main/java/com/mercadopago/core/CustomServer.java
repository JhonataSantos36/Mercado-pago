package com.mercadopago.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mercadopago.lite.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.lite.model.Discount;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.lite.preferences.CheckoutPreference;
import com.mercadopago.model.Customer;
import com.mercadopago.services.CustomService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomServer {

    public static void createCheckoutPreference(Context context, String url, String uri, Callback<CheckoutPreference> callback) {
        CustomService service = getService(context, url);
        service.createPreference(uri, null).enqueue(callback);
    }

    public static void createCheckoutPreference(Context context, String url, String uri, Map<String, Object> bodyInfo, Callback<CheckoutPreference> callback) {
        CustomService service = getService(context, url);
        service.createPreference(uri, bodyInfo).enqueue(callback);
    }

    public static void getCustomer(Context context, String url, String uri, Callback<Customer> callback) {
        CustomService service = getService(context, url);
        service.getCustomer(uri, null).enqueue(callback);
    }

    public static void getCustomer(Context context, String url, String uri, @NonNull Map<String, String> additionalInfo, Callback<Customer> callback) {
        CustomService service = getService(context, url);
        service.getCustomer(uri, additionalInfo).enqueue(callback);
    }

    public static void createPayment(Context context, String transactionId, String baseUrl, String uri,
                                     Map<String, Object> paymentData, @NonNull Map<String, String> query, Callback<Payment> callback) {
        if (query == null) {
            query = new HashMap<>();
        }
        CustomService service = getService(context, baseUrl);
        service.createPayment(transactionId, ripFirstSlash(uri), paymentData, query).enqueue(callback);
    }

    public static void getDirectDiscount(Context context, String transactionAmount, String payerEmail,String url, String uri, @NonNull Map<String, String> discountAdditionalInfo, Callback<Discount> callback) {
        CustomService service = getService(context, url);
        service.getDirectDiscount(ripFirstSlash(uri), transactionAmount, payerEmail, discountAdditionalInfo).enqueue(callback);
    }

    public static void getCodeDiscount(String discountCode, String transactionAmount, String payerEmail, Context context, String url, String uri, @NonNull Map<String, String> discountAdditionalInfo, Callback<Discount> callback) {
        CustomService service = getService(context, url);
        service.getCodeDiscount(ripFirstSlash(uri), transactionAmount, payerEmail, discountCode, discountAdditionalInfo).enqueue(callback);
    }

    private static CustomService getService(Context context, String baseUrl) {

        Retrofit retrofit = getRetrofit(context, baseUrl);
        return retrofit.create(CustomService.class);
    }

    private static Retrofit getRetrofit(Context context, String baseUrl) {

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(HttpClientUtil.getClient(context, 20, 20, 20))
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    private static String ripFirstSlash(String uri) {
        return uri.startsWith("/") ? uri.substring(1) : uri;
    }

}
