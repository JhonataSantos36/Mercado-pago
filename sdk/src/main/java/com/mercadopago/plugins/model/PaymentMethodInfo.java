package com.mercadopago.plugins.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * Created by nfortuna on 12/11/17.
 */
public class PaymentMethodInfo {


    public final String id;
    public final String name;
    public final String description;
    public final @DrawableRes int icon;

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
        this.description = null;
        this.icon = icon;
    }
}