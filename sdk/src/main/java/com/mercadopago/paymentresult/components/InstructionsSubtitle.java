package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.InstructionsSubtitleProps;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsSubtitle extends Component<InstructionsSubtitleProps, Void> {

    public InstructionsSubtitle(@NonNull final InstructionsSubtitleProps props,
                                @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
