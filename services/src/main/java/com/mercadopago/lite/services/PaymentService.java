package com.mercadopago.lite.services;

import com.mercadopago.lite.adapters.MPCall;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mromar on 10/20/17.
 */

public interface PaymentService {

    @GET("/v1/payment_methods")
    MPCall<List<PaymentMethod>> getPaymentMethods(@Query("public_key") String publicKey, @Query("access_token") String privateKey);

    @GET("/{version}/checkout/payment_methods/installments")
    MPCall<List<Installment>> getInstallments(@Path(value = "version", encoded = true) String version, @Query("public_key") String publicKey, @Query("access_token") String privateKey, @Query("bin") String bin, @Query("amount") BigDecimal amount, @Query("issuer.id") Long issuerId, @Query("payment_method_id") String paymentMethodId, @Query("locale") String locale, @Query("processing_mode") String processingMode);

    @GET("/{version}/checkout/payment_methods/card_issuers")
    MPCall<List<Issuer>> getIssuers(@Path(value = "version", encoded = true) String version, @Query("public_key") String publicKey, @Query("access_token") String privateKey, @Query("payment_method_id") String paymentMethodId, @Query("bin") String bin, @Query("processing_mode") String processingMode);

}