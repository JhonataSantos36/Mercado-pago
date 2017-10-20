package com.mercadopago.px_tracking.services;

import com.mercadopago.px_tracking.BuildConfig;
import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.PaymentIntent;
import com.mercadopago.px_tracking.model.TrackingIntent;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
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
