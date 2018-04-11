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

    public static int getPaymentMethodImage(Context context, String paymentMethodId) {

        return getPaymentMethodPicture(context, SDK_PREFIX + "img_tc_", paymentMethodId);
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
        } catch (Exception ex) {
            // do nothing
            result = ex.getMessage();
        }
        return result;
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
