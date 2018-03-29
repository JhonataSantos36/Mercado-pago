package com.mercadopago.core;

import com.mercadopago.callbacks.CallbackHolder;
import com.mercadopago.callbacks.PaymentCallback;
import com.mercadopago.callbacks.PaymentDataCallback;
import com.mercadopago.exceptions.MercadoPagoError;
import com.mercadopago.lite.model.Payment;
import com.mercadopago.model.PaymentData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by vaserber on 1/27/17.
 */

public class CallbackHolderTest {

    @Before
    public void clearContext() {
        CallbackHolderClearable.clear();
    }

    @Test
    public void setCallbackHolderPaymentCallback() {
        PaymentCallback paymentCallback = new PaymentCallback() {
            @Override
            public void onSuccess(Payment payment) {
                //Do something
            }

            @Override
            public void onCancel() {
                //Do something
            }

            @Override
            public void onFailure(MercadoPagoError exception) {
                //Do something
            }
        };

        CallbackHolder.getInstance().setPaymentCallback(paymentCallback);

        Assert.assertEquals(paymentCallback, CallbackHolder.getInstance().getPaymentCallback());
    }

    @Test
    public void setCallbackHolderPaymentDataCallback() {
        PaymentDataCallback paymentDataCallback = new PaymentDataCallback() {
            @Override
            public void onSuccess(PaymentData paymentData, boolean paymentMethodChanged) {
                //Do something
            }

            @Override
            public void onCancel() {
                //Do something
            }

            @Override
            public void onFailure(MercadoPagoError exception) {
                //Do something
            }
        };

        CallbackHolder.getInstance().setPaymentDataCallback(paymentDataCallback);

        Assert.assertEquals(paymentDataCallback, CallbackHolder.getInstance().getPaymentDataCallback());
    }

    @Test
    public void retrieveCallbackHolderSingleInstance() {
        CallbackHolder callbackHolder1 = CallbackHolder.getInstance();
        CallbackHolder callbackHolder2 = CallbackHolder.getInstance();

        Assert.assertEquals(callbackHolder1, callbackHolder2);
    }
}
