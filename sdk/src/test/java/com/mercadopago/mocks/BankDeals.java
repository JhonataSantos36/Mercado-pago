package com.mercadopago.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.BankDeal;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 8/24/17.
 */

public class BankDeals {

    private BankDeals() {

    }

    public static List<BankDeal> getBankDealsListMLA() {
        List<BankDeal> bankDealsList;
        String json = ResourcesUtil.getStringResource("bank_deals.json");

        try {
            Type listType = new TypeToken<List<BankDeal>>() {
            }.getType();
            bankDealsList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            bankDealsList = null;
        }
        return bankDealsList;
    }
}
