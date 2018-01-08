package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.model.InstructionReference;
import com.mercadopago.paymentresult.props.InstructionsReferencesProps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsReferences extends Component<InstructionsReferencesProps> {

    public InstructionsReferences(@NonNull final InstructionsReferencesProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public List<InstructionReferenceComponent> getReferenceComponents() {
        List<InstructionReferenceComponent> componentList = new ArrayList<>();

        for (InstructionReference reference: props.references) {
            final InstructionReferenceComponent.Props referenceProps = new InstructionReferenceComponent.Props.Builder()
                    .setReference(reference)
                    .build();

            final InstructionReferenceComponent component = new InstructionReferenceComponent(referenceProps, getDispatcher());

            componentList.add(component);
        }

        return componentList;
    }

}
