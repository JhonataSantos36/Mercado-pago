package com.mercadopago.review_and_confirm.props;

import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import java.math.BigDecimal;

public class AmountDescriptionProps {

    public final BigDecimal amount;
    public final String description;
    public final String currencyId;
    public final Integer textColor;
    public final String descriptionType;

    public AmountDescriptionProps(@NonNull final BigDecimal amount,
        @NonNull final String description,
        @NonNull final String currencyId,
        @NonNull final Integer textColor,
        @Nullable final String descriptionType) {

        this.amount = amount;
        this.description = description;
        this.currencyId = currencyId;
        this.textColor = textColor;
        this.descriptionType = descriptionType;
    }
}
