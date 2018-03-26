package com.mercadopago.review_and_confirm.props;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

/**
 * Created by mromar on 3/1/18.
 */

public class AmountDescriptionProps {

    public final BigDecimal amount;
    public final String description;
    public final String currencyId;
    public final Integer textColor;


    public AmountDescriptionProps(@NonNull final BigDecimal amount,
                                   @NonNull final String description,
                                   @NonNull final String currencyId,
                                   @NonNull final Integer textColor){

        this.amount = amount;
        this.description = description;
        this.currencyId = currencyId;
        this.textColor = textColor;
    }
}
