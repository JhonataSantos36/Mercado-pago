package com.mercadopago.px_tracking.mocks;

import android.content.Context;

import com.mercadopago.px_tracking.model.EventTrackIntent;
import com.mercadopago.px_tracking.model.PaymentIntent;
import com.mercadopago.px_tracking.model.TrackingIntent;
import com.mercadopago.px_tracking.services.MPTrackingService;

/**
 * Created by vaserber on 7/3/17.
 */

public class MPMockedTrackingService implements MPTrackingService {

    @Override
    public void trackPaymentId(PaymentIntent paymentIntent, Context context) {

    }

    @Override
    public void trackToken(TrackingIntent trackingIntent, Context context) {

    }

    @Override
    public void trackEvent(EventTrackIntent eventTrackIntent, Context context) {

    }
}
