package com.mercadopago.model;

import android.os.Looper;

import com.mercadopago.controllers.CheckoutTimer;

import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mromar on 11/23/16.
 */

public class TimerTest {

    @BeforeClass
    static public void initialize(){
        Looper.prepare();
    }

    @Test
    public void start() {

        CheckoutTimer.getInstance().start(2);
        CheckoutTimer.getInstance().setOnFinishListener(new CheckoutTimer.FinishListener() {
            @Override
            public void onFinish() {
                assertTrue(CheckoutTimer.getInstance().getCurrentTime().equals("00:00"));
                Thread.currentThread().interrupt();
            }
        });
    }

    @Test
    public void stop() {

        CheckoutTimer.getInstance().start(2);
        CheckoutTimer.getInstance().stop();

        assertFalse(CheckoutTimer.getInstance().isTimerEnabled());

        Thread.currentThread().interrupt();
    }

    @Test
    public void reset() {

        CheckoutTimer.getInstance().start(2);
        CheckoutTimer.getInstance().setOnFinishListener(new CheckoutTimer.FinishListener() {
            @Override
            public void onFinish() {
                CheckoutTimer.getInstance().start(2);
                assertFalse(CheckoutTimer.getInstance().isTimerEnabled());
            }
        });
    }
}
