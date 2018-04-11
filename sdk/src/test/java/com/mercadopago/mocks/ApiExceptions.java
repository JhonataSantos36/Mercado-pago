package com.mercadopago.mocks;

import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

public class ApiExceptions {

    public static ApiException getPaymentInProcessException() {
        String json = ResourcesUtil.getStringResource("payment_in_process_exception.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }
}
