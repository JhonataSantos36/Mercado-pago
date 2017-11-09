package com.mercadopago.lite.services;

import com.mercadopago.lite.adapters.MPCall;
import com.mercadopago.lite.model.Campaign;
import com.mercadopago.lite.model.Discount;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DiscountService {

    @GET("/discount_campaigns")
    MPCall<Discount> getDirectDiscount(@Query("public_key") String publicKey, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail);

    @GET("/discount_campaigns")
    MPCall<Discount> getCodeDiscount(@Query("public_key") String publicKey, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail, @Query("coupon_code") String couponCode);

    @GET("/campaigns/check_availability")
    MPCall<List<Campaign>> getCampaigns(@Query("public_key") String publicKey);
}
