package com.mercadopago.core;

import android.content.Context;

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.callbacks.Callback;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.preferences.ServicePreference;
import com.mercadopago.services.CustomService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vaserber on 1/25/17.
 */

public class CustomServiceHandler {

    public static void createCheckoutPreference(Context context, Callback<CheckoutPreference> callback) {
        //TODO pedir al contexto la ServicePreference
        Map<String, Object> map = new HashMap<>();
        map.put("item_id", "1");
        map.put("amount", new BigDecimal(300));
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setCreateCheckoutPreferenceURL("http://private-4d9654-mercadopagoexamples.apiary-mock.com",
                        "/merchantUri/create_preference", map)
                .build();

        String checkoutPreferenceURL = servicePreference.getCreateCheckoutPreferenceURL();
        String checkoutPreferenceURI = servicePreference.getCreateCheckoutPreferenceURI();
        Map<String, Object> additionalInfo = servicePreference.getCreateCheckoutPreferenceAdditionalInfo();

        CustomService service = getService(context, checkoutPreferenceURL);
        service.createPreference(checkoutPreferenceURI, additionalInfo).enqueue(callback);
    }

    public static void getCustomer(Context context, Callback<Customer> callback) {
        //TODO pedir al contexto la ServicePreference
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setCreateCheckoutPreferenceURL("/baseUrl", "/Uri")
                .build();

        String getCustomerURL = servicePreference.getGetCustomerURL();
        String getCustomerURI = servicePreference.getGetCustomerURI();
        Map<String, String> additionalInfo = servicePreference.getGetCustomerAdditionalInfo();

        CustomService service = getService(context, getCustomerURL);
        service.getCustomer(getCustomerURI, additionalInfo).enqueue(callback);
    }

    public static void createPayment(Context context, String transactionId, Callback<Payment> callback) {
        //TODO pedir al contexto la ServicePreference
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setCreateCheckoutPreferenceURL("/baseUrl", "/Uri")
                .build();

        String createPaymentURL = servicePreference.getCreatePaymentURL();
        String createPaymentURI = servicePreference.getCreatePaymentURI();
        Map<String, Object> additionalInfo = servicePreference.getCreatePaymentAdditionalInfo();

        createPayment(context, transactionId, createPaymentURL, createPaymentURI, additionalInfo, callback);
    }

    public static void createPayment(Context context, String transactionId, String baseUrl, String uri, Map<String, Object> paymentData, Callback<Payment> callback) {
        CustomService service = getService(context, baseUrl);
        service.createPayment(transactionId, ripFirstSlash(uri), paymentData).enqueue(callback);
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
