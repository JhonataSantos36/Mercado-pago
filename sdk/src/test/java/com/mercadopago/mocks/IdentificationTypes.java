package com.mercadopago.mocks;

import com.mercadopago.model.IdentificationType;

public class IdentificationTypes {
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
}
