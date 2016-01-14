package com.mercadopago.services;

import com.mercadopago.model.Customer;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface CustomerService {

    @GET("/customers")
    void getCustomer(@Query("preference_id") String preferenceId, Callback<Customer> callback);
}