package com.mercadopago.mocks;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.lite.exceptions.ApiException;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.utils.ResourcesUtil;

import java.lang.reflect.Type;
import java.util.List;

public class IdentificationTypes {

    private static String doNotFindIdentificationTypesException = "{\"message\":\"doesn't find identification types\",\"error\":\"identification types not found error\",\"cause\":[]}";


    public static IdentificationType getById(String id) {
        switch (id) {
            case "RUT":
                return new IdentificationType("RUT", "RUT", "string", 7, 20);
            case "CPF":
                return new IdentificationType("CPF", "CPF", "number", 11, 11);
            default:
                return new IdentificationType("DNI", "DNI", "number", 7, 8);
        }
    }

    public static ApiException getDoNotFindIdentificationTypesException() {
        return JsonUtil.getInstance().fromJson(doNotFindIdentificationTypesException, ApiException.class);
    }

    public static IdentificationType getIdentificationType() {
        List<IdentificationType> identificationTypesList;
        String json = ResourcesUtil.getStringResource("identification_types.json");

        try {
            Type listType = new TypeToken<List<IdentificationType>>() {
            }.getType();
            identificationTypesList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            identificationTypesList = null;
        }
        return identificationTypesList.get(0);
    }


    public static List<IdentificationType> getIdentificationTypes() {
        List<IdentificationType> identificationTypesList;
        String json = ResourcesUtil.getStringResource("identification_types.json");

        try {
            Type listType = new TypeToken<List<IdentificationType>>() {
            }.getType();
            identificationTypesList = JsonUtil.getInstance().getGson().fromJson(json, listType);
        } catch (Exception ex) {
            identificationTypesList = null;
        }
        return identificationTypesList;
    }
}
