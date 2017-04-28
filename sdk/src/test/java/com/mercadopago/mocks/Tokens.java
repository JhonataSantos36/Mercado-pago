package com.mercadopago.mocks;

import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

/**
 * Created by vaserber on 4/24/17.
 */

public class Tokens {

    private Tokens() {}

    public static Token getToken() {
        String json = ResourcesUtil.getStringResource("token.json");
        return JsonUtil.getInstance().fromJson(json, Token.class);
    }
}
