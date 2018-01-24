package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;

/**
 * Created by mromar on 11/22/17.
 */

public class PaymentMethodRenderer extends Renderer<PaymentMethod> {
    @Override
    public View render(final PaymentMethod component, final Context context) {
        final View paymentMethodView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_method_component, null, false);
        final ViewGroup paymentMethodViewGroup = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodContainer);
        final ImageView imageView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodIcon);
        final MPTextView descriptionTextView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodDescription);
        final MPTextView detailTextView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodDetail);
        final MPTextView statementDescriptionTextView = paymentMethodView.findViewById(R.id.mpsdkStatementDescription);
        final FrameLayout totalAmountContainer = paymentMethodView.findViewById(R.id.mpsdkTotalAmountContainer);

        imageView.setImageDrawable(ContextCompat.getDrawable(context, component.getIconResource()));

        final Renderer totalAmountRenderer = RendererFactory.create(context, component.getTotalAmountComponent());
        final View amountView = totalAmountRenderer.render();
        totalAmountContainer.addView(amountView);

        setText(descriptionTextView, component.getDescription());
        setText(detailTextView, component.getDetail());
        setText(statementDescriptionTextView, component.getDisclaimer());

        stretchHeight(paymentMethodViewGroup);
        return paymentMethodView;
    }
}
