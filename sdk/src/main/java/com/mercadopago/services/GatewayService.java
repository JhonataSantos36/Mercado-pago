package com.mercadopago.services;

import com.mercadopago.model.CardToken;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

public interface GatewayService {

    @POST("/v1/card_tokens")
    void getToken(@Query("public_key") String publicKey, @Body CardToken cardToken, Callback<Token> callback);

    @POST("/v1/card_tokens")
    void getToken(@Query("public_key") String publicKey, @Body SavedCardToken savedCardToken, Callback<Token> callback);

}