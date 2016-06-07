package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.CheckoutPreference;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Instruction;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentIntent;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaymentService {

    @GET("/v1/payment_methods")
    MPCall<List<PaymentMethod>> getPaymentMethods(@Query("public_key") String publicKey);

    @GET("/beta/checkout/payment_methods/search/options")
    MPCall<PaymentMethodSearch> getPaymentMethodSearch(@Query("public_key") String publicKey, @Query("amount") BigDecimal amount, @Query("excluded_payment_types") String excludedPaymentTypes, @Query("excluded_payment_methods") String excludedPaymentMethods);

    @GET("/v1/payment_methods/installments")
    MPCall<List<Installment>> getInstallments(@Query("public_key") String publicKey, @Query("bin") String bin, @Query("amount") BigDecimal amount, @Query("issuer.id") Long issuerId, @Query("payment_method_id") String paymentMethodId, @Query("locale") String locale);

    @GET("/v1/payment_methods/card_issuers")
    MPCall<List<Issuer>> getIssuers(@Query("public_key") String publicKey, @Query("payment_method_id") String paymentMethodId, @Query("bin") String bin);

    @POST("/beta/checkout/payments")
    MPCall<Payment> createPayment(@Header("X-Idempotency-Key") String transactionId, @Body PaymentIntent body);

    @GET("/beta/checkout/payments/{payment_id}/results")
    MPCall<Instruction> getInstruction(@Path(value = "payment_id", encoded = true) Long paymentId, @Query("public_key") String mKey, @Query("payment_type") String paymentTypeId);

    @GET("/beta/checkout/preferences/{preference_id}")
    MPCall<CheckoutPreference> getPreference(@Path(value = "preference_id", encoded = true) String checkoutPreferenceId, @Query("public_key") String publicKey);
}