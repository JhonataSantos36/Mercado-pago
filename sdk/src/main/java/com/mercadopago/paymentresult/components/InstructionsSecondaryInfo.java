package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.InstructionsSecondaryInfoProps;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsSecondaryInfo extends Component<InstructionsSecondaryInfoProps, Void> {

    public InstructionsSecondaryInfo(@NonNull final InstructionsSecondaryInfoProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

}
