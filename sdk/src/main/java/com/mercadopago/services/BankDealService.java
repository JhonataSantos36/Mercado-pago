package com.mercadopago.services;

import com.mercadopago.model.BankDeal;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface BankDealService {

    @GET("/v1/payment_methods/deals")
    void getBankDeals(@Query("public_key") String publicKey, @Query("locale") String locale, Callback<List<BankDeal>> callback);
}