package com.mercadopago.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.Issuer;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 4/24/17.
 */

public class Issuers {

    private Issuers() {}

    public static Issuer getIssuer() {
        String json = ResourcesUtil.getStringResource("issuer.json");
        return JsonUtil.getInstance().fromJson(json, Issuer.class);
    }

    public static List<Issuer> getIssuersList() {
        List<Issuer> issuerList;
        String json = ResourcesUtil.getStringResource("issuers.json");

        try {
            Type listType = new TypeToken<List<Issuer>>() {
            }.getType();
            issuerList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            issuerList = null;
        }
        return issuerList;
    }
}
