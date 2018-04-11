package com.mercadopago.mocks;


import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

public class Tokens {
    private Tokens() {}

    public static Token getVisaToken() {
        String json = ResourcesUtil.getStringResource("token_visa.json");
        return JsonUtil.getInstance().fromJson(json, Token.class);
    }

    public static Token getToken() {
        String json = ResourcesUtil.getStringResource("token.json");
        return JsonUtil.getInstance().fromJson(json, Token.class);
    }

    public static Token getTokenWithESC() {
        String json = ResourcesUtil.getStringResource("token_with_esc.json");
        return JsonUtil.getInstance().fromJson(json, Token.class);
    }

    public static ApiException getInvalidTokenWithESC() {
        String json = ResourcesUtil.getStringResource("invalid_token_with_esc.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }

    public static ApiException getInvalidTokenWithESCFingerprint() {
        String json = ResourcesUtil.getStringResource("invalid_token_with_esc_fingerprint.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }

    public static ApiException getInvalidCloneToken() {
        String json = ResourcesUtil.getStringResource("invalid_clone_token.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }

    public static ApiException getInvalidCreateToken() {
        String json = ResourcesUtil.getStringResource("invalid_clone_token.json");
        return JsonUtil.getInstance().fromJson(json, ApiException.class);
    }
}
