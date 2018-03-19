package com.mercadopago.tracker;

import android.support.annotation.NonNull;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
import com.mercadopago.plugins.model.PaymentMethodInfo;

import java.util.List;

/**
 * Created by marlanti on 3/12/18.
 */

public class TrackingFormatter {

    private static final int MAX_LENGTH = 3000;
    private static String internPrefix = ":";
    private static String externPrefix = "";


    public static String getFormattedPaymentMethodsForTracking(@NonNull PaymentMethodSearch paymentMethodSearch, @NonNull List<PaymentMethodInfo> pluginInfoList) {
        List<PaymentMethod> paymentMethods = paymentMethodSearch.getPaymentMethods();
        List<CustomSearchItem> customSearchItems = paymentMethodSearch.getCustomSearchItems();

        StringBuilder formatted = new StringBuilder(MAX_LENGTH);

        externPrefix = "";
        formatted = formatPaymentMethods(formatted, paymentMethods);
        formatted = formatSavedCards(formatted, customSearchItems);
        formatted = formatPaymentMethodPlugins(formatted, pluginInfoList);


        return formatted.toString();
    }

    private static StringBuilder formatSavedCards(@NonNull StringBuilder formatted, @NonNull List<CustomSearchItem> customSearchItems) {

        for (CustomSearchItem customSearchItem : customSearchItems) {
            formatted.append(externPrefix);
            formatted.append(customSearchItem.getPaymentMethodId());
            formatted.append(internPrefix);
            formatted.append(customSearchItem.getType());
            formatted.append(internPrefix);
            formatted.append(customSearchItem.getId());
            externPrefix = "|";
        }

        return formatted;
    }

    private static StringBuilder formatPaymentMethods(@NonNull StringBuilder formatted, @NonNull List<PaymentMethod> paymentMethods) {

        for (PaymentMethod p : paymentMethods) {
            formatted.append(externPrefix);
            formatted.append(p.getId());
            formatted.append(internPrefix);
            formatted.append(p.getPaymentTypeId());
            externPrefix = "|";
        }
        return formatted;
    }

    private static StringBuilder formatPaymentMethodPlugins(@NonNull StringBuilder formatted, @NonNull List<PaymentMethodInfo> pluginInfoList) {
        if (!pluginInfoList.isEmpty()) {
            for (PaymentMethodInfo info : pluginInfoList) {
                formatted.append(externPrefix);
                formatted.append(info.getId());
                formatted.append(internPrefix);
                formatted.append(PaymentTypes.PLUGIN);
                externPrefix = "|";
            }
        }

        return formatted;
    }
}
