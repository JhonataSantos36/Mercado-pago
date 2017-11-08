package com.mercadopago.tracking.services;

import com.mercadopago.tracking.model.EventTrackIntent;
import com.mercadopago.tracking.model.PaymentIntent;
import com.mercadopago.tracking.model.TrackingIntent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by vaserber on 6/5/17.
 */

public interface TrackingService {

    @POST("/{version}/checkout/tracking")
    Call<Void> trackToken(@Path(value = "version", encoded = true) String version, @Body TrackingIntent body);

    @POST("/{version}/checkout/tracking/off")
    Call<Void> trackPaymentId(@Path(value = "version", encoded = true) String version, @Body PaymentIntent body);

    @POST("/{version}/checkout/tracking/events")
    Call<Void> trackEvents(@Path(value = "version", encoded = true) String version, @Body EventTrackIntent body);
}
