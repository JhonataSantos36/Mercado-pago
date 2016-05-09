package com.mercadopago.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.mercadopago.ErrorActivity;
import com.mercadopago.R;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.model.PaymentMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MercadoPagoUtil {

    public static int getPaymentMethodIcon(Context context, String paymentMethodId) {

        return getPaymentMethodPicture(context, "", paymentMethodId);
    }

    public static int getPaymentMethodImage(Context context, String paymentMethodId) {

        return getPaymentMethodPicture(context, "img_tc_", paymentMethodId);
    }

    private static int getPaymentMethodPicture(Context context, String type, String paymentMethodId) {

        int resource;
        paymentMethodId = type + paymentMethodId;
        try {
            resource = context.getResources().getIdentifier(paymentMethodId, "drawable", context.getPackageName());
        }
        catch (Exception e) {
            try {
                resource = context.getResources().getIdentifier("bank", "drawable", context.getPackageName());
            }
            catch (Exception ex) {
                resource = 0;
            }
        }
        return resource;
    }

    public static int getPaymentMethodSearchItemIcon(Context context, String itemId) {
        int resource;
        if(itemId != null && context != null) {
            if (itemId.equals("7eleven")) {
               itemId = "seven_eleven";
            }
            try {
               resource = context.getResources().getIdentifier(itemId, "drawable", context.getPackageName());
            } catch (Exception e) {
               resource = 0;
            }
        } else {
            resource = 0;
        }
        return resource;
    }

    public static String getCVVDescriptor(Context context, PaymentMethod paymentMethod) {

        if ("amex".equals(paymentMethod.getId())) {
            return String.format(context.getString(com.mercadopago.R.string.mpsdk_cod_seg_desc_amex), 4);
        } else {
            return String.format(context.getString(com.mercadopago.R.string.mpsdk_cod_seg_desc), 3);
        }
    }

    public static int getCVVImageResource(Context context, PaymentMethod paymentMethod) {

        return getPaymentMethodImage(context, paymentMethod.getId());
    }

    public static String formatDate(Context context, Date date) {

        String result;
        try {
            result = new SimpleDateFormat("dd MM yyyy HH:mm").format(date);
            String[] splitString = result.split(" ");
            result = context.getString(R.string.mpsdk_format_date, splitString[0], splitString[1], splitString[2], splitString[3]);
        }
        catch (Exception ex) {
            // do nothing
            result = ex.getMessage();
        }
        return result;
    }

    public static boolean isCardPaymentType(String paymentTypeId) {

        if ((paymentTypeId != null) && (paymentTypeId.equals("credit_card") || paymentTypeId.equals("debit_card") || paymentTypeId.equals("prepaid_card"))) {
            return true;
        } else {
            return false;
        }
    }

    public static String getAccreditationTimeMessage(int miliseconds, Context context) {

        String accreditationMessage;

        if(miliseconds == 0) {
            accreditationMessage = context.getString(R.string.mpsdk_instant_accreditation_time);
        } else {
            StringBuilder accreditationTimeMessageBuilder = new StringBuilder();
            if (miliseconds > 1440 && miliseconds < 2880) {

                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_accreditation_time));
                accreditationTimeMessageBuilder.append(" 1 ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_working_day));

            }  else if(miliseconds < 1440){

                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(miliseconds/60);
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_hour));

            } else{

                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(miliseconds/(60*24));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_working_days));
            }
            accreditationMessage = accreditationTimeMessageBuilder.toString();
        }
        return accreditationMessage;
    };
}
