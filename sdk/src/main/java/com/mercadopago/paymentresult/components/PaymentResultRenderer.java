package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

/**
 * Created by vaserber on 10/20/17.
 */

public class PaymentResultRenderer extends Renderer<PaymentResultContainer> {

    @Override
    public View render(final PaymentResultContainer component, final Context context) {
        View view;

        if (component.isLoading()) {

            view = RendererFactory.create(context, component.getLoadingComponent()).render();

        } else {

            view = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_container, null, false);
            final ViewGroup parentViewGroup = view.findViewById(R.id.mpsdkPaymentResultContainer);

            final Renderer headerRenderer = RendererFactory.create(context, component.getHeaderComponent());
            final View header = headerRenderer.render();
            parentViewGroup.addView(header);

            if (component.hasBodyComponent()) {
                final Renderer bodyRenderer = RendererFactory.create(context, component.getBodyComponent());
                final View body = bodyRenderer.render();
                parentViewGroup.addView(body);
            }

            parentViewGroup.addView(RendererFactory.create(context, component.getFooterContainer()).render());
        }

        return view;
    }
}