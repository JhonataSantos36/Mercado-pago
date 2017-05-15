package com.mercadopago.utils;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.Campaign;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.ResourcesUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by mromar on 5/4/17.
 */

public class Campaigns {

    private Campaigns() {
    }

    public static List<Campaign> getCampaigns() {
        List<Campaign> campaigns;
        String json = ResourcesUtil.getStringResource("campaigns.json");

        try {
            Type listType = new TypeToken<List<Campaign>>() {
            }.getType();
            campaigns = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            campaigns = null;
        }

        return campaigns;
    }
}
