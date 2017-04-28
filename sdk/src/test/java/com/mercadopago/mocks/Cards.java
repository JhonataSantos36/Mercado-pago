package com.mercadopago.mocks;

import com.mercadopago.model.Card;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

/**
 * Created by vaserber on 4/21/17.
 */

public class Cards {

    private Cards() {}

    public static Card getCard() {
        String json = ResourcesUtil.getStringResource("card.json");
        return JsonUtil.getInstance().fromJson(json, Card.class);
    }
}
