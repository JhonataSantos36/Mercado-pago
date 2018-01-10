package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.LayoutInflater;
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
    public View render(InstructionsReferences component, Context context) {
        final View referencesView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_references, null, false);
        final ViewGroup referencesViewGroup = referencesView.findViewById(R.id.mpsdkInstructionsReferencesContainer);
        final MPTextView referencesTitle = referencesView.findViewById(R.id.mpsdkInstructionsReferencesTitle);

        setText(referencesTitle, component.props.title);

        List<InstructionReferenceComponent> referenceComponentList = component.getReferenceComponents();
        for (InstructionReferenceComponent instructionReferenceComponent: referenceComponentList) {
            final Renderer referenceRenderer = RendererFactory.create(context, instructionReferenceComponent);
            final View reference = referenceRenderer.render();
            referencesViewGroup.addView(reference);
        }

        return referencesView;
    }
}
