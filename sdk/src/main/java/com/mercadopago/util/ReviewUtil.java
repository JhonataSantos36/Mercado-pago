package com.mercadopago.util;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;

/**
 * Created by vaserber on 11/9/16.
 */

public class ReviewUtil {

    private ReviewUtil() {

    }

    public static int getPaymentInstructionTemplate(PaymentMethod paymentMethod) {
        int resource;
        String key = paymentMethod.getId() + "_" + paymentMethod.getPaymentTypeId();
        if (key.startsWith("pagofacil")) {
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
        } else if (key.startsWith("davivienda")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("efecty")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("movilred")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("viabaloto")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("servipag")) {
            resource = R.string.mpsdk_review_off_text_6;
        } else if (key.startsWith("webpay")) {
            resource = R.string.mpsdk_review_off_text_5;
        } else if (key.startsWith("mercantil_atm")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("mercantil_bank_transfer")) {
            resource = R.string.mpsdk_review_off_text_3;
        } else if (key.startsWith("provincial")) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.startsWith("account_money")) {
            resource = R.string.mpsdk_review_off_text_4;
        } else if (key.startsWith("pagoefectivo_atm") && !(key.contains("bank_transfer"))) {
            resource = R.string.mpsdk_review_off_text;
        } else if (key.endsWith("bank_transfer")) {
            resource = R.string.mpsdk_review_off_text_3;
        } else {
            resource = R.string.mpsdk_review_off_text_default;
        }
        return resource;
    }

    public static String getPaymentMethodDescription(PaymentMethod paymentMethod, String description, Context context) {
        String string;
        String key = paymentMethod.getId() + "_" + paymentMethod.getPaymentTypeId();
        String transformedKey = transform(key);
        switch (transformedKey) {
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
            case "pagoefectivo_atm":
                string = context.getResources().getString(R.string.mpsdk_your_atm) + " ";
                string += TextUtil.isEmpty(description) ? paymentMethod.getName() : description;
                break;
            case "pagoefectivo_atm_bank_transfer":
                string = context.getResources().getString(R.string.mpsdk_homebanking) + " ";
                string += TextUtil.isEmpty(description) ? paymentMethod.getName() : description;
                break;
            case "davivienda":
                string = paymentMethod.getName();
                break;
            case "efecty":
                string = paymentMethod.getName();
                break;
            case "movilred":
                string = paymentMethod.getName();
                break;
            case "viabaloto":
                string = paymentMethod.getName();
                break;
            case "mercantil_atm":
                string = context.getResources().getString(R.string.mpsdk_your_atm) + " ";
                string += TextUtil.isEmpty(description) ? paymentMethod.getName() : description;
                break;
            case "mercantil_bank_transfer":
                string = context.getResources().getString(R.string.mpsdk_homebanking) + " ";
                string += TextUtil.isEmpty(description) ? paymentMethod.getName() : description;
                break;
            case "provincial":
                string = paymentMethod.getName();
                break;
            case "servipag_ticket":
                string = paymentMethod.getName();
                break;
            case "webpay_bank_transfer":
                string = paymentMethod.getName();
                break;
            case "account_money_account_money":
                string = context.getString(R.string.mpsdk_ryc_account_money_description);
                string += TextUtil.isEmpty(description) ? paymentMethod.getName() : description;
                break;
            default:
                string = paymentMethod.getName();
                break;
        }
        return string;
    }

    private static String transform(String key) {
        if (key.contains("bank_transfer") && key.contains("pagoefectivo_atm")) {
            return "pagoefectivo_atm_bank_transfer";
        } else if (key.contains("pagoefectivo_atm")) {
            return "pagoefectivo_atm";
        } else {
            return key;
        }
    }

}
