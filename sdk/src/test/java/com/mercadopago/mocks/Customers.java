package com.mercadopago.mocks;

import com.mercadopago.model.Customer;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

public class Customers {
    public static Customer getCustomerWithCards() {
        String json = ResourcesUtil.getStringResource("customer_cards.json");
        return JsonUtil.getInstance().fromJson(json, Customer.class);
    }
}
