package com.mercadopago.tracking.services;

import com.mercadopago.tracking.BuildConfig;
import com.mercadopago.tracking.model.EventTrackIntent;
import com.mercadopago.tracking.model.PaymentIntent;
import com.mercadopago.tracking.model.TrackingIntent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by vaserber on 6/5/17.
 */

public interface TrackingService {

    @POST("/" + BuildConfig.API_VERSION + "/checkout/tracking")
    Call<Void> trackToken(@Body TrackingIntent body);

    @POST("/" + BuildConfig.API_VERSION + "/checkout/tracking/off")
    Call<Void> trackPaymentId(@Body PaymentIntent body);

    @POST("/" + BuildConfig.API_VERSION + "/checkout/tracking/events")
    Call<Void> trackEvents(@Body EventTrackIntent body);
}
