package com.mercadopago.services;

import com.mercadopago.model.CheckoutIntent;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Discount;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.Payment;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface MerchantService {

    @GET("/{uri}")
    void getCustomer(@EncodedPath("uri") String uri, @Query("merchant_access_token") String merchantAccessToken, Callback<Customer> callback);

    @POST("/{uri}")
    void createPayment(@EncodedPath("uri") String uri, @Body MerchantPayment body, Callback<Payment> callback);

    @POST("/{uri}")
    void createPreference(@EncodedPath("uri") String uri, @Body CheckoutIntent body, Callback<CheckoutPreference> callback);
}
