package com.mercadopago.px_tracking.services;

import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.PaymentIntent;
import com.mercadopago.px_tracking.model.TrackingIntent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by vaserber on 6/5/17.
 */

public interface TrackingService {

    @POST("/v1/checkout/tracking")
    Call<Void> trackToken(@Body TrackingIntent body);

    @POST("/v1/checkout/tracking/off")
    Call<Void> trackPaymentId(@Body PaymentIntent body);

    @POST("/v1/checkout/tracking/events")
    Call<Void> trackEvents(@Body EventTrackIntent body);
}
