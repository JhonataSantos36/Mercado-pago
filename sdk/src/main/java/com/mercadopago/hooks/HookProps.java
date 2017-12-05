package com.mercadopago.hooks;

import com.mercadopago.model.PaymentData;
import com.mercadopago.model.PaymentResult;

/**
 * Created by nfortuna on 10/25/17.
 */

public class HookProps {

    public final PaymentData paymentData;
    public final PaymentResult paymentResult;

    public HookProps(PaymentData paymentData, PaymentResult paymentResult) {
        this.paymentData = paymentData;
        this.paymentResult = paymentResult;
    }


}
