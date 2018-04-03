package com.mercadopago.tracker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.model.CustomSearchItem;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.PaymentMethodSearch;
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


    public static String getFormattedPaymentMethodsForTracking(@Nullable PaymentMethodSearch paymentMethodSearch, @NonNull List<PaymentMethodInfo> pluginInfoList, Set<String> escCardIds) {

        StringBuilder formatted = new StringBuilder(MAX_LENGTH);

        externPrefix = "";

        if (paymentMethodSearch != null) {
            final List<PaymentMethod> paymentMethods = paymentMethodSearch.getPaymentMethods();
            formatted = formatPaymentMethods(formatted, paymentMethods);
            final List<CustomSearchItem> customSearchItems = paymentMethodSearch.getCustomSearchItems();
            formatted = formatSavedCards(formatted, customSearchItems, escCardIds);
        }

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

    private static StringBuilder formatPaymentMethods(@NonNull StringBuilder formatted, @Nullable List<PaymentMethod> paymentMethods) {
        if (paymentMethods != null) {
            for (PaymentMethod p : paymentMethods) {
                formatted.append(externPrefix);
                formatted.append(p.getId());
                formatted.append(internPrefix);
                formatted.append(p.getPaymentTypeId());
                externPrefix = "|";
            }
        }
        return formatted;
    }

    private static StringBuilder formatPaymentMethodPlugins(@NonNull StringBuilder formatted, @Nullable List<PaymentMethodInfo> pluginInfoList) {
        if (pluginInfoList != null) {
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
