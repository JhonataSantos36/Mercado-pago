package com.mercadopago.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mreverter on 15/1/16.
 */
public class PaymentType implements Serializable {

    public static String CREDIT_CARD = "credit_card";
    public static String DEBIT_CARD = "debit_card";
    public static String PREPAID_CARD = "prepaid_card";
    public static String TICKET = "ticket";
    public static String ATM = "atm";
    public static String DIGITAL_CURRENCY = "digital_currency";
    public static String BANK_TRANSFER = "bank_transfer";

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static List<String> getAllPaymentTypes() {
        return new ArrayList<String>(){{
            add(CREDIT_CARD);
            add(DEBIT_CARD);
            add(PREPAID_CARD);
            add(TICKET);
            add(ATM);
            add(DIGITAL_CURRENCY);
            add(BANK_TRANSFER);
        }};
    }
}
