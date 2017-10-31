package com.mercadopago.lite.services;

import com.mercadopago.lite.adapters.MPCall;
import com.mercadopago.lite.model.IdentificationType;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mromar on 10/20/17.
 */

public interface IdentificationService {

    @GET("/v1/identification_types")
    MPCall<List<IdentificationType>> getIdentificationTypes(@Query("public_key") String publicKey, @Query("access_token") String privateKey);

}
