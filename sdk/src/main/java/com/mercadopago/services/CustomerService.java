package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.Customer;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CustomerService {

    @GET("/customers")
    MPCall<Customer> getCustomer(@Query("preference_id") String preferenceId);
}