package com.mercadopago.services;

import com.mercadopago.lite.adapters.MPCall;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface MerchantService {

    @GET("/{uri}")
    MPCall<Customer> getCustomer(@Path(value="uri", encoded = true) String uri, @Query(value="merchant_access_token", encoded = true) String merchantAccessToken);

    @POST("/{uri}")
    MPCall<Payment> createPayment(@Path(value="uri", encoded = true) String uri, @Body MerchantPayment body);

    @POST("/{uri}")
    MPCall<CheckoutPreference> createPreference(@Path(value = "uri", encoded = true) String uri, @Body Map<String, Object> body);

    @GET("/{uri}")
    MPCall<Discount> getDirectDiscount(@Path(value = "uri", encoded = true) String uri, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail, @QueryMap(encoded = true) Map<String, String> discountAdditionalInfo);

    @GET("/{uri}")
    MPCall<Discount> getCodeDiscount(@Path(value = "uri", encoded = true) String uri, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail, @Query("coupon_code") String couponCode, @QueryMap(encoded = true) Map<String, String> discountAdditionalInfo);
}
