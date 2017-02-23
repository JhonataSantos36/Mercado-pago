package com.mercadopago.services;

import com.mercadopago.adapters.MPCall;
import com.mercadopago.model.BankDeal;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BankDealService {

    @GET("/v1/payment_methods/deals")
    MPCall<List<BankDeal>> getBankDeals(@Query("public_key") String publicKey, @Query("access_token") String privateKey, @Query("locale") String locale);
}