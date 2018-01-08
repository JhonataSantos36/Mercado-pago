package com.mercadopago.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by mromar on 5/5/17.
 */

public class PayerCosts {

    private PayerCosts() {
    }

    public static List<PayerCost> getPayerCostsWithCFT() {
        List<PayerCost> payerCosts;
        String json = ResourcesUtil.getStringResource("payer_costs_with_cft_and_tea.json");

        try {
            Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            payerCosts = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            payerCosts = null;
        }
        return payerCosts;
    }

    public static List<PayerCost> getPayerCostList() {
        List<PayerCost> payerCostList;
        String json = ResourcesUtil.getStringResource("payer_costs.json");

        try {
            Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            payerCostList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            payerCostList = null;
        }
        return payerCostList;
    }

    public static List<PayerCost> getOnePayerCostList() {
        List<PayerCost> payerCosts;
        String json = ResourcesUtil.getStringResource("payer_cost_list.json");

        try {
            Type listType = new TypeToken<List<PayerCost>>() {
            }.getType();
            payerCosts = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            payerCosts = null;
        }
        return payerCosts;
    }

    public static PayerCost getPayerCost() {
        String json = ResourcesUtil.getStringResource("payer_cost.json");
        return JsonUtil.getInstance().fromJson(json, PayerCost.class);
    }

    public static PayerCost getPayerCostWithoutInstallments() {
        String json = ResourcesUtil.getStringResource("payer_cost_one_installment.json");
        return JsonUtil.getInstance().fromJson(json, PayerCost.class);
    }
}
