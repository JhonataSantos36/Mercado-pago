package com.mercadopago.paymentresult.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.paymentresult.components.Instructions;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsRenderer extends Renderer<Instructions> {


    @Override
    public View render() {
        final View instructionsView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions, null, false);
        final ViewGroup parentViewGroup = instructionsView.findViewById(R.id.mpsdkInstructionsContainer);

        if (component.hasSubtitle()) {
            final Renderer subtitleRenderer = RendererFactory.create(context, component.getSubtitleComponent());
            final View subtitle = subtitleRenderer.render();
            parentViewGroup.addView(subtitle);
        }

        final Renderer contentRenderer = RendererFactory.create(context, component.getContentComponent());
        final View content = contentRenderer.render();
        parentViewGroup.addView(content);

        //TODO backend refactor: secondary info should be an email related component
        if (component.hasSecondaryInfo() && component.shouldShowEmailInSecondaryInfo()) {
            final Renderer secondaryInfoRenderer = RendererFactory.create(context, component.getSecondaryInfoComponent());
            final View secondaryInfo = secondaryInfoRenderer.render();
            parentViewGroup.addView(secondaryInfo);
        }

        return instructionsView;
    }
}
