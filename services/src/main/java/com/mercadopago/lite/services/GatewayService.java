package com.mercadopago.lite.services;

import com.mercadopago.lite.adapters.MPCall;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.SavedESCCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.model.requests.SecurityCodeIntent;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GatewayService {

    @POST("/v1/card_tokens")
    MPCall<Token> getToken(@Query("public_key") String publicKey, @Query("access_token") String privateKey, @Body CardToken cardToken);

    @POST("/v1/card_tokens")
    MPCall<Token> getToken(@Query("public_key") String publicKey, @Query("access_token") String privateKey, @Body SavedCardToken savedCardToken);

    @POST("/v1/card_tokens")
    MPCall<Token> getToken(@Query("public_key") String publicKey, @Query("access_token") String privateKey, @Body SavedESCCardToken savedESCCardToken);

    @POST("/v1/card_tokens/{token_id}/clone")
    MPCall<Token> getToken(@Path(value = "token_id") String tokenId, @Query("public_key") String publicKey, @Query("access_token") String privateKey);

    @PUT("/v1/card_tokens/{token_id}")
    MPCall<Token> getToken(@Path(value = "token_id") String tokenId, @Query("public_key") String publicKey, @Query("access_token") String privateKey, @Body SecurityCodeIntent securityCodeIntent);
}