package com.mercadopago.model;

import com.mercadopago.constants.PaymentTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentType implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static List<String> getAllPaymentTypes() {
        return new ArrayList<String>(){{
            add(PaymentTypes.CREDIT_CARD);
            add(PaymentTypes.DEBIT_CARD);
            add(PaymentTypes.PREPAID_CARD);
            add(PaymentTypes.TICKET);
            add(PaymentTypes.ATM);
            add(PaymentTypes.DIGITAL_CURRENCY);
            add(PaymentTypes.BANK_TRANSFER);
        }};
    }
}
