package com.mercadopago.paymentresult.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActionsRenderer extends Renderer<InstructionsActions> {

    @Override
    public View render(final InstructionsActions component, final Context context, final ViewGroup parent) {
        final View actionsView = inflate(R.layout.mpsdk_payment_result_instructions_actions, parent);
        final ViewGroup parentViewGroup = actionsView.findViewById(R.id.mpsdkInstructionsActionsContainer);

        List<InstructionsAction> actionComponentList = component.getActionComponents();
        for (InstructionsAction instructionsAction : actionComponentList) {
            RendererFactory.create(context, instructionsAction).render(parentViewGroup);
        }

        return actionsView;
    }
}
