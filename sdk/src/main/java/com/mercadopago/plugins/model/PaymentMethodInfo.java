package com.mercadopago.plugins.model;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.mercadopago.plugins.PaymentMethodPlugin;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodInfo {

    public final String id;
    public final String name;
    public final String description;
    @DrawableRes public final
    int icon;

    public PaymentMethodInfo(@NonNull final String id,
                             @NonNull final String name,
                             @DrawableRes final int icon,
                             @NonNull final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public PaymentMethodInfo(@NonNull final String id,
                             @NonNull final String name,
                             @DrawableRes final int icon) {

        this.id = id;
        this.name = name;
        description = null;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }


    @NonNull
    public static List<PaymentMethodInfo> getPluginsPaymentMethodInfo(final Context context, final List<PaymentMethodPlugin> paymentMethodPlugins) {
        List<PaymentMethodInfo> list = new ArrayList<>();

        for (PaymentMethodPlugin plugin : paymentMethodPlugins) {
            final PaymentMethodInfo info = plugin.getPaymentMethodInfo(context);
            list.add(info);
        }

        return list;
    }
}