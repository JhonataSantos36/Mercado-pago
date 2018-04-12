package com.mercadopago.model.requests;

import com.mercadopago.model.Payer;

public class PayerIntent {
    private Payer payer;

    public PayerIntent(Payer payer) {
        this.payer = payer;
    }
}
