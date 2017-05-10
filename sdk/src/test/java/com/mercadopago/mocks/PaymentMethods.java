package com.mercadopago.mocks;

import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

/**
 * Created by mromar on 5/4/17.
 */

public class PaymentMethods {

    private PaymentMethods() {
    }

    public static PaymentMethod getPaymentMethodOn() {
        String json = ResourcesUtil.getStringResource("payment_method_on.json");
        return JsonUtil.getInstance().fromJson(json, PaymentMethod.class);
    }
}
