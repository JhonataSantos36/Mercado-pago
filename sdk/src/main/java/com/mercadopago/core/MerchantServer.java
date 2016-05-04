package com.mercadopago.core;

import android.content.Context;

import com.mercadopago.model.CheckoutIntent;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Customer;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.services.MerchantService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.util.Dictionary;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class MerchantServer {

    public static void createPreference(Context context, String merchantBaseUrl, String merchantCreatePreferenceUri, Map<String, Object> checkoutData, Callback<CheckoutPreference> callback) {

        MerchantService service = getService(context, merchantBaseUrl);
        service.createPreference(ripFirstSlash(merchantCreatePreferenceUri), checkoutData, callback);
    }

    public static void getCustomer(Context context, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken, Callback<Customer> callback) {

        MerchantService service = getService(context, merchantBaseUrl);
        service.getCustomer(ripFirstSlash(merchantGetCustomerUri), merchantAccessToken, callback);
    }

    public static void createPayment(Context context, String merchantBaseUrl, String merchantCreatePaymentUri, MerchantPayment payment, final Callback<Payment> callback) {

        MerchantService service = getService(context, merchantBaseUrl);
        service.createPayment(ripFirstSlash(merchantCreatePaymentUri), payment, callback);
    }

    private static String ripFirstSlash(String uri) {

        return uri.startsWith("/") ? uri.substring(1, uri.length()) : uri;
    }

    private static RestAdapter getRestAdapter(Context context, String endPoint) {

        return new RestAdapter.Builder()
                .setEndpoint(endPoint)
                .setLogLevel(Settings.RETROFIT_LOGGING)
                .setConverter(new GsonConverter(JsonUtil.getInstance().getGson()))
                .setClient(HttpClientUtil.getClient(context))
                .build();
    }

    private static MerchantService getService(Context context, String endPoint) {

        RestAdapter restAdapter = getRestAdapter(context, endPoint);
        return restAdapter.create(MerchantService.class);
    }
}
