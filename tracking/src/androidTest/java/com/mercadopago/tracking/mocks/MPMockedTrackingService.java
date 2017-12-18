package com.mercadopago.tracking.mocks;

import android.content.Context;

import com.mercadopago.tracking.model.EventTrackIntent;
import com.mercadopago.tracking.model.PaymentIntent;
import com.mercadopago.tracking.model.TrackingIntent;
import com.mercadopago.tracking.services.MPTrackingService;

import retrofit2.Callback;

/**
 * Created by vaserber on 7/3/17.
 */

public class MPMockedTrackingService implements MPTrackingService {

    @Override
    public void trackPaymentId(PaymentIntent paymentIntent, Context context) {

    }

    @Override
    public void trackEvents(String publicKey, EventTrackIntent eventTrackIntent, Context context) {

    }

    @Override
    public void trackEvents(String publicKey, EventTrackIntent eventTrackIntent, Context context, Callback<Void> callback) {

    }

    @Override
    public void trackToken(TrackingIntent trackingIntent, Context context) {

    }
}
