package com.mercadopago.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mromar on 6/23/16.
 */
public class PaymentTypes {

    public static String CREDIT_CARD = "credit_card";
    public static String DEBIT_CARD = "debit_card";
    public static String PREPAID_CARD = "prepaid_card";
    public static String TICKET = "ticket";
    public static String ATM = "atm";
    public static String DIGITAL_CURRENCY = "digital_currency";
    public static String BANK_TRANSFER = "bank_transfer";
    @Deprecated
    public static String ACCOUNT_MONEY = "account_money";
    public static String PLUGIN = "payment_method_plugin";

    private PaymentTypes(){}

    public static List<String> getAllPaymentTypes() {
        return new ArrayList<String>(){{
            add(CREDIT_CARD);
            add(DEBIT_CARD);
            add(PREPAID_CARD);
            add(TICKET);
            add(ATM);
            add(DIGITAL_CURRENCY);
            add(BANK_TRANSFER);
            add(ACCOUNT_MONEY);
        }};
    }
}
