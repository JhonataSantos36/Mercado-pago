package com.mercadopago.model;

/**
 * Created by mreverter on 11/14/16.
 */
public class PayerIntent {
    private Payer payer;

    public PayerIntent(Payer payer) {
        this.payer = payer;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }
}
