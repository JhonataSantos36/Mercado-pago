package com.mercadopago.controllers;

public interface Timer {
    void start(long seconds);
    void stop();
    Boolean isTimerEnabled();
    void setOnFinishListener(FinishListener listener);
    void finishCheckout();

    interface FinishListener {
        void onFinish();
    }
}
