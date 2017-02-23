package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.Customer;
import com.mercadopago.model.Payment;
import com.mercadopago.preferences.CheckoutPreference;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by vaserber on 1/24/17.
 */

public interface CustomService {

    @POST("/{uri}")
    MPCall<CheckoutPreference> createPreference(@Path(value = "uri", encoded = true) String uri,
                                                @Body Map<String, Object> additionalInfo);

    @POST("/{uri}")
    MPCall<Payment> createPayment(@Header("X-Idempotency-Key") String transactionId,
                                  @Path(value = "uri", encoded = true) String uri,
                                  @Body Map<String, Object> additionalInfo);

    @GET("/{uri}")
    MPCall<Customer> getCustomer(@Path(value = "uri", encoded = true) String uri,
                                 @QueryMap(encoded = true) Map<String, String> additionalInfo);

}
