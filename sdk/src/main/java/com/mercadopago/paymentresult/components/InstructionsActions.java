package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.model.InstructionActionInfo;
import com.mercadopago.paymentresult.props.InstructionsActionsProps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsActions extends Component<InstructionsActionsProps> {

    public InstructionsActions(@NonNull final InstructionsActionsProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public List<InstructionsAction> getActionComponents() {
        List<InstructionsAction> componentList = new ArrayList<>();

        for (InstructionActionInfo actionInfo: props.instructionActions) {

            if (actionInfo.getTag().equals(InstructionActionInfo.Tags.LINK)) {
                final InstructionsAction.Prop actionProp = new InstructionsAction.Prop.Builder()
                        .setInstructionActionInfo(actionInfo)
                        .build();
                final InstructionsAction component = new InstructionsAction(actionProp, getDispatcher());
                componentList.add(component);
            }
        }

        return componentList;
    }
}
