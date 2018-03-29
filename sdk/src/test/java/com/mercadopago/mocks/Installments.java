package com.mercadopago.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.lite.model.Installment;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

import java.lang.reflect.Type;
import java.util.List;

public class Installments {

    private static String doNotFindInstallmentsException = "{\"message\":\"doesn't find installments\",\"error\":\"installments not found error\",\"cause\":[]}";

    private Installments() {
    }

    public static Installment getInstallments() {
        String json = ResourcesUtil.getStringResource("installments.json");
        return JsonUtil.getInstance().fromJson(json, Installment.class);
    }

    public static List<Installment> getInstallmentsList() {
        List<Installment> installmentList;
        String json = ResourcesUtil.getStringResource("installments_list.json");

        try {
            Type listType = new TypeToken<List<Installment>>() {
            }.getType();
            installmentList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            installmentList = null;
        }
        return installmentList;
    }

    public static List<Installment> getInstallmentsListWithUniquePayerCost() {
        List<Installment> installmentList;
        String json = ResourcesUtil.getStringResource("installments_unique.json");

        try {
            Type listType = new TypeToken<List<Installment>>() {
            }.getType();
            installmentList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            installmentList = null;
        }
        return installmentList;
    }

    public static List<Installment> getInstallmentsListWithMultiplePayerCost() {
        List<Installment> installmentList;
        String json = ResourcesUtil.getStringResource("installments_multiple.json");

        try {
            Type listType = new TypeToken<List<Installment>>() {
            }.getType();
            installmentList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            installmentList = null;
        }
        return installmentList;
    }

    public static List<Installment> getInstallmentsListWithoutPayerCosts() {
        List<Installment> installmentList;
        String json = ResourcesUtil.getStringResource("installments_list_without_payer_costs.json");

        try {
            Type listType = new TypeToken<List<Installment>>() {
            }.getType();
            installmentList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            installmentList = null;
        }
        return installmentList;
    }

    public static ApiException getDoNotFindInstallmentsException() {
        return JsonUtil.getInstance().fromJson(doNotFindInstallmentsException, ApiException.class);
    }
}
