package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
    public View render(final PaymentMethod component, final Context context, final ViewGroup parent) {
        final View paymentMethodView = inflate(R.layout.mpsdk_payment_method_component, parent);
        final ViewGroup paymentMethodViewGroup = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodContainer);
        final ImageView imageView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodIcon);
        final MPTextView descriptionTextView = paymentMethodView.findViewById(R.id.mpsdkPaymentMethodDescription);
        final MPTextView statementDescriptionTextView = paymentMethodView.findViewById(R.id.mpsdkStatementDescription);
        final FrameLayout totalAmountContainer = paymentMethodView.findViewById(R.id.mpsdkTotalAmountContainer);

        imageView.setImageDrawable(ContextCompat.getDrawable(context, component.getIconResource()));

        RendererFactory.create(context, component.getTotalAmountComponent()).render(totalAmountContainer);

        setText(descriptionTextView, component.getDescription());
        setText(statementDescriptionTextView, component.getDisclaimer());

        stretchHeight(paymentMethodViewGroup);
        return paymentMethodView;
    }
}
