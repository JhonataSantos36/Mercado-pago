package com.mercadopago.tracker;

import android.support.annotation.NonNull;

import com.mercadopago.lite.model.PaymentTypes;
import com.mercadopago.lite.model.CustomSearchItem;
import com.mercadopago.lite.model.PaymentMethod;
import com.mercadopago.lite.model.PaymentMethodSearch;
import com.mercadopago.plugins.model.PaymentMethodInfo;

import java.util.List;
import java.util.Set;

/**
 * Created by marlanti on 3/12/18.
 */

public class TrackingFormatter {

    private static final int MAX_LENGTH = 3000;
    private static final String internPrefix = ":";
    private static String externPrefix = "";
    private static final String ESC_PREFIX = "ESC";


    public static String getFormattedPaymentMethodsForTracking(@NonNull PaymentMethodSearch paymentMethodSearch, @NonNull List<PaymentMethodInfo> pluginInfoList, Set<String> escCardIds) {
        List<PaymentMethod> paymentMethods = paymentMethodSearch.getPaymentMethods();
        List<CustomSearchItem> customSearchItems = paymentMethodSearch.getCustomSearchItems();

        StringBuilder formatted = new StringBuilder(MAX_LENGTH);

        externPrefix = "";
        formatted = formatPaymentMethods(formatted, paymentMethods);
        formatted = formatSavedCards(formatted, customSearchItems, escCardIds);
        formatted = formatPaymentMethodPlugins(formatted, pluginInfoList);


        return formatted.toString();
    }

    private static StringBuilder formatSavedCards(@NonNull StringBuilder formatted, @NonNull List<CustomSearchItem> customSearchItems, Set<String> escCardIds) {

        for (CustomSearchItem customSearchItem : customSearchItems) {
            formatted.append(externPrefix);
            formatted.append(customSearchItem.getPaymentMethodId());
            formatted.append(internPrefix);
            formatted.append(customSearchItem.getType());
            formatted.append(internPrefix);
            formatted.append(customSearchItem.getId());
            if(escCardIds != null && escCardIds.contains(customSearchItem.getId())){
                formatted.append(internPrefix);
                formatted.append(ESC_PREFIX);
            }
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
