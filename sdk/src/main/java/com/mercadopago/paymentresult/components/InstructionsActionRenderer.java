package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.mercadopago.R;
import com.mercadopago.components.LinkAction;
import com.mercadopago.components.Renderer;
import com.mercadopago.customviews.MPTextView;
import com.mercadopago.model.InstructionActionInfo;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActionRenderer extends Renderer<InstructionsAction> {

    @Override
    public View render(final InstructionsAction component, final Context context) {
        final View actionView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_action, null, false);
        final MPTextView actionTextView = actionView.findViewById(R.id.instructionAction);

        if (component.props.instructionActionInfo.getTag().equals(InstructionActionInfo.Tags.LINK)) {
            actionTextView.setText(component.props.instructionActionInfo.getLabel());

            actionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    component.getDispatcher().dispatch(new LinkAction(component.props.instructionActionInfo.getUrl()));
                }
            });
        }

        return actionView;
    }
}
