package com.mercadopago.plugins.model;


public interface Processor {

    void process(BusinessPayment businessPayment);

    void process(GenericPayment pluginPaymentResult);
}
