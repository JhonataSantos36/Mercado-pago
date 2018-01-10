package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.InstructionsInfoProps;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsInfo extends Component<InstructionsInfoProps, Void> {

    public InstructionsInfo(@NonNull final InstructionsInfoProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
