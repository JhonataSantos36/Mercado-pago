package com.mercadopago.core;

import android.content.Context;

import com.mercadopago.lite.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.lite.callbacks.Callback;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.lite.preferences.CheckoutPreference;
import com.mercadopago.services.MerchantService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Deprecated
public class MerchantServer {

    public static void createPreference(Context context, String merchantBaseUrl, String merchantCreatePreferenceUri, Map<String, Object> checkoutData, Callback<CheckoutPreference> callback) {

        MerchantService service = getService(context, merchantBaseUrl);
        service.createPreference(ripFirstSlash(merchantCreatePreferenceUri), checkoutData).enqueue(callback);
    }

    public static void createPayment(Context context, String merchantBaseUrl, String merchantCreatePaymentUri, MerchantPayment payment, Callback<Payment> callback) {
        MerchantService service = getService(context, merchantBaseUrl);
        service.createPayment(ripFirstSlash(merchantCreatePaymentUri), payment).enqueue(callback);
    }

    public static void getCustomer(Context context, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken, Callback<Customer> callback) {
        MerchantService service = getService(context, merchantBaseUrl);
        service.getCustomer(ripFirstSlash(merchantGetCustomerUri), merchantAccessToken).enqueue(callback);
    }

    public static void getDirectDiscount(String transactionAmount, String payerEmail, Context context, String merchantBaseUrl, String merchantGetDirectDiscountUri, Map<String, String> discountAdditionalInfo, Callback<Discount> callback) {

        MerchantService service = getService(context, merchantBaseUrl);
        service.getDirectDiscount(ripFirstSlash(merchantGetDirectDiscountUri), transactionAmount, payerEmail, getDiscountAdditionalInfo(discountAdditionalInfo)).enqueue(callback);
    }

    public static void getCodeDiscount(String discountCode, String transactionAmount, String payerEmail, Context context, String merchantBaseUrl, String merchantGetCodeDiscountUri, Map<String, String> discountAdditionalInfo, Callback<Discount> callback) {

        MerchantService service = getService(context, merchantBaseUrl);
        service.getCodeDiscount(ripFirstSlash(merchantGetCodeDiscountUri), transactionAmount, payerEmail, discountCode, getDiscountAdditionalInfo(discountAdditionalInfo)).enqueue(callback);
    }

    private static String ripFirstSlash(String uri) {

        return uri.startsWith("/") ? uri.substring(1, uri.length()) : uri;
    }

    private static Retrofit getRetrofit(Context context, String endPoint) {

        return new Retrofit.Builder()
                .baseUrl(endPoint)
                .client(HttpClientUtil.getClient(context, 20, 20, 20))
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    private static MerchantService getService(Context context, String endPoint) {

        Retrofit retrofit = getRetrofit(context, endPoint);
        return retrofit.create(MerchantService.class);
    }

    private static Map<String, String> getDiscountAdditionalInfo(Map<String, String> discountAdditionalInfo) {
        Map<String, String> map = new HashMap<>();

        return discountAdditionalInfo == null ? map : discountAdditionalInfo;
    }
}
