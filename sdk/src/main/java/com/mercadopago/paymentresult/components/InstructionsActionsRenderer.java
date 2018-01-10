package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.paymentresult.components.InstructionsAction;
import com.mercadopago.paymentresult.components.InstructionsActions;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActionsRenderer extends Renderer<InstructionsActions> {

    @Override
    public View render(InstructionsActions component, Context context) {
        final View actionsView = LayoutInflater.from(context).inflate(R.layout.mpsdk_payment_result_instructions_actions, null, false);
        final ViewGroup parentViewGroup = actionsView.findViewById(R.id.mpsdkInstructionsActionsContainer);

        List<InstructionsAction> actionComponentList = component.getActionComponents();
        for (InstructionsAction instructionsAction : actionComponentList) {
            final Renderer actionRenderer = RendererFactory.create(context, instructionsAction);
            final View action = actionRenderer.render();
            parentViewGroup.addView(action);
        }

        return actionsView;
    }
}
