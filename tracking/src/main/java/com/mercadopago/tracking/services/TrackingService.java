package com.mercadopago.tracking.services;

import com.mercadopago.tracking.model.EventTrackIntent;
import com.mercadopago.tracking.model.PaymentIntent;
import com.mercadopago.tracking.model.TrackingIntent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by vaserber on 6/5/17.
 */

public interface TrackingService {

    @POST("/{version}/checkout/tracking")
    Call<Void> trackToken(@Path(value = "version", encoded = true) String version, @Body TrackingIntent body);

    @POST("/{version}/checkout/tracking/off")
    Call<Void> trackPaymentId(@Path(value = "version", encoded = true) String version, @Body PaymentIntent body);

    @POST("/{version}/checkout/tracking/events")
    Call<Void> trackEvents(@Header("Accept-version") String eventsTrackingVersion, @Path(value = "version", encoded = true) String version, @Query("public_key") String publicKey, @Body EventTrackIntent body);
}
