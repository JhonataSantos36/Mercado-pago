package com.mercadopago.util;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;

/**
 * Created by vaserber on 11/9/16.
 */

public class ReviewUtil {
    protected ReviewUtil() {

    }

    public static int getPaymentInstructionTemplate(PaymentMethod paymentMethod) {
        int resource;
        String key = paymentMethod.getId() + "_" + paymentMethod.getPaymentTypeId();
        if(key.startsWith("pagofacil")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("rapipago")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("bapropagos")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("redlink_atm")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("bancomer_7eleven")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("bancomer_atm")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("banamex_telecomm")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("serfin_atm")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("banamex_atm")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("oxxo")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("cargavirtual")) {
            resource = R.string.mpsdk_review_off_text_2;
        } else if (key.startsWith("redlink_bank_transfer")) {
            resource = R.string.mpsdk_review_off_text_2;
        } else if (key.startsWith("bancomer_bank_transfer")) {
            resource = R.string.mpsdk_review_off_text_3;
        } else if (key.startsWith("banamex_bank_transfer")) {
            resource = R.string.mpsdk_review_off_text_3;
        } else if (key.startsWith("serfin_bank_transfer")) {
            resource = R.string.mpsdk_review_off_text_3;
        } else if (key.startsWith("bolbradesco")) {
            resource = R.string.mpsdk_review_off_text_4;

        } else if (key.startsWith("account_money")) {
            resource = R.string.mpsdk_review_off_text_4;
        } else {
            resource = R.string.mpsdk_review_off_text_default;
        }
        return resource;
    }

    public static String getPaymentMethodDescription(PaymentMethod paymentMethod, Context context) {
        String string;
        String key = paymentMethod.getId() + "_" + paymentMethod.getPaymentTypeId();
        switch (key) {
            case "bapropagos":
                string = "Provincia NET";
                break;
            case "pagofacil":
                string = paymentMethod.getName();
                break;
            case "rapipago":
                string = paymentMethod.getName();
                break;
            case "redlink_atm":
                string = context.getResources().getString(R.string.mpsdk_your_atm) + " " + paymentMethod.getName();
                break;
            case "redlink_bank_transfer":
                string = context.getResources().getString(R.string.mpsdk_homebanking) + " " + paymentMethod.getName();
                break;
            case "bancomer_7eleven":
                string = "7 Eleven";
                break;
            case "bancomer_atm":
                string = context.getResources().getString(R.string.mpsdk_your_atm) + " " + paymentMethod.getName();
                break;
            case "banamex_telecomm":
                string = "Telecomm";
                break;
            case "serfin_atm":
                string = paymentMethod.getName();
                break;
            case "banamex_atm":
                string = context.getResources().getString(R.string.mpsdk_your_atm) + " " + paymentMethod.getName();
                break;
            case "oxxo":
                string = paymentMethod.getName();
                break;
            case "bancomer_bank_transfer":
                string = context.getResources().getString(R.string.mpsdk_homebanking) + " " + paymentMethod.getName();
                break;
            case "banamex_bank_transfer":
                string = context.getResources().getString(R.string.mpsdk_homebanking) + " " + paymentMethod.getName();
                break;
            case "serfin_bank_transfer":
                string = context.getResources().getString(R.string.mpsdk_homebanking) + " " + paymentMethod.getName();
                break;
            case "bolbradesco":
                string = "boleto";
                break;
            case "account_money_account_money":
                string = context.getString(R.string.mpsdk_ryc_account_money_description);
                break;
            default:
                string = paymentMethod.getName();
                break;
        }
        return string;
    }
}
