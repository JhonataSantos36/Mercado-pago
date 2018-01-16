package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsContentRenderer extends Renderer<InstructionsContent> {

    @Override
    public View render(final InstructionsContent component, final Context context) {
        final View instructionsView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_content, null, false);
        final ViewGroup parentViewGroup = instructionsView.findViewById(R.id.mpsdkInstructionsContentContainer);
        final View bottomMarginView = instructionsView.findViewById(R.id.mpsdkContentBottomMargin);

        if (component.needsBottomMargin()) {
            bottomMarginView.setVisibility(View.VISIBLE);
        } else {
            bottomMarginView.setVisibility(View.GONE);
        }

        if (component.hasInfo()) {
            final Renderer infoRenderer = RendererFactory.create(context, component.getInfoComponent());
            final View info = infoRenderer.render();
            parentViewGroup.addView(info);
        }
        if (component.hasReferences()) {
            final Renderer referencesRenderer = RendererFactory.create(context, component.getReferencesComponent());
            final View references = referencesRenderer.render();
            parentViewGroup.addView(references);
        }
        if (component.hasTertiaryInfo()) {
            final Renderer tertiaryInfoRenderer = RendererFactory.create(context, component.getTertiaryInfoComponent());
            final View tertiaryInfo = tertiaryInfoRenderer.render();
            parentViewGroup.addView(tertiaryInfo);
        }
        if (component.hasAccreditationTime()) {
            final Renderer accreditationTimeRenderer = RendererFactory.create(context, component.getAccreditationTimeComponent());
            final View accreditationTime = accreditationTimeRenderer.render();
            parentViewGroup.addView(accreditationTime);
        }
        if (component.hasActions()) {
            final Renderer actionsRenderer = RendererFactory.create(context, component.getActionsComponent());
            final View actions = actionsRenderer.render();
            parentViewGroup.addView(actions);
        }

        return instructionsView;
    }
}
