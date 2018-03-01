package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.customviews.MPTextView;

import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsReferencesRenderer extends Renderer<InstructionsReferences> {

    @Override
    public View render(final InstructionsReferences component, final Context context, final ViewGroup parent) {

        final View referencesView = inflate(R.layout.mpsdk_payment_result_instructions_references, parent);
        final ViewGroup referencesViewGroup = referencesView.findViewById(R.id.mpsdkInstructionsReferencesContainer);
        final MPTextView referencesTitle = referencesView.findViewById(R.id.mpsdkInstructionsReferencesTitle);

        setText(referencesTitle, component.props.title);

        /*
         * If we call render with the parent inside a for loop, only the first view is displayed, don't know why,
         * for now we use addView manually to avoid the issue.
         */
        final List<InstructionReferenceComponent> referenceComponentList = component.getReferenceComponents();
        for (final InstructionReferenceComponent instructionReferenceComponent : referenceComponentList) {
            final View reference = RendererFactory.create(context, instructionReferenceComponent).render(null);
            referencesViewGroup.addView(reference);
        }

        return referencesView;
    }
}
