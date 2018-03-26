package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;

/**
 * Created by mromar on 11/27/17.
 */

public class TotalAmountRenderer extends Renderer<TotalAmount> {

    @Override
    public View render(final TotalAmount component, final Context context, final ViewGroup parent) {
        final View bodyView = inflate(R.layout.mpsdk_total_amount_component, parent);
        final MPTextView amountTitleTextView = bodyView.findViewById(R.id.mpsdkAmountTitle);
        final MPTextView amountDetailTextView = bodyView.findViewById(R.id.mpsdkAmountDetail);
        setText(amountTitleTextView, component.getAmountTitle());
        setText(amountDetailTextView, component.getAmountDetail());

        return bodyView;
    }
}
