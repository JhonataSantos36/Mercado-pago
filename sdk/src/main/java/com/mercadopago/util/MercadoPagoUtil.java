package com.mercadopago.util;

import android.content.Context;

import com.mercadopago.R;
import com.mercadopago.exceptions.BinException;
import com.mercadopago.model.Bin;
import com.mercadopago.model.PaymentMethod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MercadoPagoUtil {

    private static final String SDK_PREFIX = "mpsdk_";

    public static int getPaymentMethodIcon(Context context, String paymentMethodId) {

        return getPaymentMethodPicture(context, SDK_PREFIX, paymentMethodId);
    }

    private static int getPaymentMethodPicture(Context context, String type, String paymentMethodId) {

        int resource;
        paymentMethodId = type + paymentMethodId;
        try {
            resource = context.getResources().getIdentifier(paymentMethodId, "drawable", context.getPackageName());
        } catch (Exception e) {
            try {
                resource = context.getResources().getIdentifier(SDK_PREFIX + "bank", "drawable", context.getPackageName());
            } catch (Exception ex) {
                resource = 0;
            }
        }
        return resource;
    }

    public static int getPaymentMethodSearchItemIcon(Context context, String itemId) {
        int resource;
        if (itemId != null && context != null) {
            try {
                resource = context.getResources().getIdentifier(SDK_PREFIX + itemId, "drawable", context.getPackageName());
            } catch (Exception e) {
                resource = 0;
            }
        } else {
            resource = 0;
        }
        return resource;
    }

    public static boolean isCard(String paymentTypeId) {

        return (paymentTypeId != null) && (paymentTypeId.equals("credit_card") || paymentTypeId.equals("debit_card") ||
            paymentTypeId.equals("prepaid_card"));
    }

    public static String getAccreditationTimeMessage(Context context, int milliseconds) {

        String accreditationMessage;

        if (milliseconds == 0) {
            accreditationMessage = context.getString(R.string.mpsdk_instant_accreditation_time);
        } else {
            StringBuilder accreditationTimeMessageBuilder = new StringBuilder();
            if (milliseconds > 1440 && milliseconds < 2880) {

                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_accreditation_time));
                accreditationTimeMessageBuilder.append(" 1 ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_working_day));

            } else if (milliseconds < 1440) {

                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(milliseconds / 60);
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_hour));

            } else {

                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(milliseconds / (60 * 24));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.mpsdk_working_days));
            }
            accreditationMessage = accreditationTimeMessageBuilder.toString();
        }
        return accreditationMessage;
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(String bin, List<PaymentMethod> paymentMethods) {
        if (bin.length() == Bin.BIN_LENGTH) {
            List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        }

        throw new BinException(bin.length());
    }
}
