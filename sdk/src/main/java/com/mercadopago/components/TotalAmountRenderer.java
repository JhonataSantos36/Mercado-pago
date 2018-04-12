package com.mercadopago.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.customviews.MPTextView;

public class TotalAmountRenderer extends Renderer<TotalAmount> {

    @Override
    public View render(@NonNull final TotalAmount component, @NonNull final Context context, final ViewGroup parent) {
        final View bodyView = inflate(R.layout.mpsdk_total_amount_component, parent);
        final MPTextView amountTitleTextView = bodyView.findViewById(R.id.mpsdkAmountTitle);
        final MPTextView amountDetailTextView = bodyView.findViewById(R.id.mpsdkAmountDetail);
        setText(amountTitleTextView, component.getAmountTitle());
        setText(amountDetailTextView, component.getAmountDetail());
        return bodyView;
    }
}
