package com.mercadopago.lite.model.requests;

import com.mercadopago.lite.model.Payer;

public class PayerIntent {
    private Payer payer;

    public PayerIntent(Payer payer) {
        this.payer = payer;
    }
}
