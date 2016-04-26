package com.mercadopago.services;

import com.mercadopago.model.Installment;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;

import java.math.BigDecimal;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface PaymentService {

    @GET("/v1/payment_methods")
    void getPaymentMethods(@Query("public_key") String publicKey, Callback<List<PaymentMethod>> callback);

    @GET("/beta/checkout/payment_methods/search/options")
    void getPaymentMethodSearch(@Query("public_key") String publicKey, @Query("amount") BigDecimal amount, @Query("excluded_payment_types") String excludedPaymentTypes, @Query("excluded_payment_methods") String excludedPaymentMethods, Callback<PaymentMethodSearch> callback);

    @GET("/v1/payment_methods/installments")
    void getInstallments(@Query("public_key") String publicKey, @Query("bin") String bin, @Query("amount") BigDecimal amount, @Query("issuer.id") Long issuerId, @Query("payment_type_id") String paymentTypeId, @Query("locale") String locale, Callback<List<Installment>> callback);

    @GET("/v1/payment_methods/card_issuers")
    void getIssuers(@Query("public_key") String publicKey, @Query("payment_method_id") String paymentMethodId, Callback<List<Issuer>> callback);

    @GET("/beta/checkout/native_payment")
    void createPayment(@Query("public_key") String publicKey, @Query("pref_id") String preferenceId, @Query("email") String email, @Query("payment_method_id") String paymentMethodId, @Query("installments") Integer installments, @Query("issuer") String issuer, @Query("token") String token, Callback<Payment> callback);

    @GET("/beta/checkout/instructions/{payment_id}")
    void getInstruction(@Query("public_key") String mKey, @EncodedPath("payment_id") Long paymentId, @Query("payment_method_id") String paymentMethodId, @Query("payment_type_id") String paymentTypeId, Callback<Instruction> callback);
}