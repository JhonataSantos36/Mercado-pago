package com.mercadopago.services;

import com.mercadopago.model.IdentificationType;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface IdentificationService {

    @GET("/v1/identification_types")
    void getIdentificationTypes(@Query("public_key") String publicKey, @Query("access_token") String privateKey, Callback<List<IdentificationType>> callback);

}
