package com.mercadopago.utils;

import com.mercadopago.model.DummyIdentificationType;

/**
 * Created by vaserber on 8/3/16.
 */
public class IdentificationTestUtils {

    public static DummyIdentificationType getDummyIdentificationType(String id) {
        switch (id) {
            case "DNI":
                return new DummyIdentificationType("DNI", "12345678", "12.345.678");
            case "CPF":
                return new DummyIdentificationType("CPF", "12312312344", "123.123.123-44");
            case "CNPJ":
                return new DummyIdentificationType("CNPJ", "57215433000181", "57.215.433/0001-81");
            default:
                return null;
        }
    }
}
