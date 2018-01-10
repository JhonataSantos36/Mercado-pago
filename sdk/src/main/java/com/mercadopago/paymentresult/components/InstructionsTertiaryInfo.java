package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.InstructionsTertiaryInfoProps;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsTertiaryInfo extends Component<InstructionsTertiaryInfoProps, Void> {

    public InstructionsTertiaryInfo(@NonNull final InstructionsTertiaryInfoProps props,
                                    @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
