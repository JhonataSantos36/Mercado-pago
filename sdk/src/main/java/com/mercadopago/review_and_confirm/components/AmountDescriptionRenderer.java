package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.util.CurrenciesUtil;

/**
 * Created by mromar on 2/28/18.
 */

public class AmountDescriptionRenderer extends Renderer<AmountDescription> {

    @Override
    public View render(@NonNull final AmountDescription component, @NonNull final Context context, final ViewGroup parent) {
        final View bodyView = inflate(R.layout.mpsdk_amount_description_component, parent);
        final MPTextView descriptionTextView = bodyView.findViewById(R.id.mpsdkDescription);
        final MPTextView amountTextView = bodyView.findViewById(R.id.mpsdkAmount);

        setText(descriptionTextView, component.props.description);
        setText(amountTextView, CurrenciesUtil.getSpannedAmountWithCurrencySymbol(component.props.amount, component.props.currencyId));

        descriptionTextView.setTextColor(component.props.textColor);
        amountTextView.setTextColor(component.props.textColor);

        return bodyView;
    }
}
