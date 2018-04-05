package com.mercadopago.review_and_confirm.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.SummaryItemType;
import com.mercadopago.util.CurrenciesUtil;

public class AmountDescriptionRenderer extends Renderer<AmountDescription> {

    @Override
    public View render(@NonNull final AmountDescription component, @NonNull final Context context, final ViewGroup parent) {
        final View bodyView = inflate(R.layout.mpsdk_amount_description_component, parent);
        final MPTextView descriptionTextView = bodyView.findViewById(R.id.mpsdkDescription);
        final MPTextView amountTextView = bodyView.findViewById(R.id.mpsdkAmount);

        setText(descriptionTextView, component.props.description);

        final StringBuilder amountBuilder = new StringBuilder();
        if (SummaryItemType.DISCOUNT.equals(component.props.descriptionType)) {
            amountBuilder.append("-");
        }
        final Spanned spannedAmount = CurrenciesUtil.getSpannedAmountWithCurrencySymbol(component.props.amount, component.props.currencyId);

        amountTextView.setText(TextUtils.concat(amountBuilder, spannedAmount));

        descriptionTextView.setTextColor(component.props.textColor);
        amountTextView.setTextColor(component.props.textColor);

        return bodyView;
    }
}
