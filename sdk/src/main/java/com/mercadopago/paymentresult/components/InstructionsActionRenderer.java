package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.LinkAction;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.InstructionAction;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActionRenderer extends Renderer<InstructionsAction> {

    @Override
    public View render(final InstructionsAction component, final Context context, final ViewGroup parent) {
        final View actionView = inflate(R.layout.mpsdk_payment_result_instructions_action, parent);
        final MPTextView actionTextView = actionView.findViewById(R.id.instructionAction);

        if (component.props.instructionAction.getTag().equals(InstructionAction.Tags.LINK)) {
            actionTextView.setText(component.props.instructionAction.getLabel());

            actionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    component.getDispatcher().dispatch(new LinkAction(component.props.instructionAction.getUrl()));
                }
            });
        }

        return actionView;
    }
}
