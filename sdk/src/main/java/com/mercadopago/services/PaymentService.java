package com.mercadopago.services;

import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface PaymentService {

    @GET("/v1/payment_methods")
    void getPaymentMethods(@Query("public_key") String publicKey, Callback<List<PaymentMethod>> callback);

    @GET("/v1/payment_methods/installments")
    void getInstallments(@Query("public_key") String publicKey, @Query("bin") String bin, @Query("amount") BigDecimal amount, @Query("issuer.id") Long issuerId, @Query("payment_type_id") String paymentTypeId, @Query("locale") String locale, Callback<List<Installment>> callback);

    @GET("/v1/payment_methods/card_issuers")
    void getIssuers(@Query("public_key") String publicKey, @Query("payment_method_id") String paymentMethodId, Callback<List<Issuer>> callback);

    @POST("/payments")
    void createPayment(@Body PaymentIntent body, Callback<Payment> callback);

}