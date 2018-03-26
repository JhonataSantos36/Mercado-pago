package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

/**
 * Created by vaserber on 10/23/17.
 */

public class BodyRenderer extends Renderer<Body> {
    @Override
    public View render(@NonNull final Body component, @NonNull final Context context, final ViewGroup parent) {
        final View bodyView = inflate(R.layout.mpsdk_payment_result_body, parent);
        final ViewGroup bodyViewGroup = bodyView.findViewById(R.id.mpsdkPaymentResultContainerBody);

        if (component.hasInstructions()) {
            RendererFactory.create(context, component.getInstructionsComponent()).render(bodyViewGroup);
        } else if (component.hasBodyError()) {
            RendererFactory.create(context, component.getBodyErrorComponent()).render(bodyViewGroup);
        } else {
            if (component.hasReceipt()) {
                RendererFactory.create(context, component.getReceiptComponent()).render(bodyViewGroup);
            }
            if (component.hasTopCustomComponent()) {
                RendererFactory.create(context, component.getApprovedTopCustomComponent()).render(bodyViewGroup);
            }
            if (component.hasPaymentMethodDescription()) {
                RendererFactory.create(context, component.getPaymentMethodComponent()).render(bodyViewGroup);
            }
            if (component.hasBottomCustomComponent()) {
                RendererFactory.create(context, component.getApprovedBottomCustomComponent()).render(bodyViewGroup);
            }
        }
        stretchHeight(bodyViewGroup);
        return bodyView;
    }
}