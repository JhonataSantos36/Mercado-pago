package com.mercadopago.lite.services;

import com.mercadopago.lite.adapters.MPCall;
import com.mercadopago.lite.model.Customer;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mromar on 10/20/17.
 */

public interface CustomerService {

    @GET("/customers")
    MPCall<Customer> getCustomer(@Query("preference_id") String preferenceId);
}