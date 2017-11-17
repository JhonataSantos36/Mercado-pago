package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.Campaign;
import com.mercadopago.model.Discount;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mromar on 11/29/16.
 */

public interface DiscountService {

    @GET("/discount_campaigns")
    MPCall<Discount> getDirectDiscount(@Query("public_key") String publicKey, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail);

    @GET("/discount_campaigns")
    MPCall<Discount> getCodeDiscount(@Query("public_key") String publicKey, @Query("transaction_amount") String transactionAmount, @Query("email") String payerEmail, @Query("coupon_code") String couponCode);

    @GET("/campaigns/check_availability")
    MPCall<List<Campaign>> getCampaigns(@Query("public_key") String publicKey);
}
