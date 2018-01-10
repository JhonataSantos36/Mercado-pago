package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.IconProps;

/**
 * Created by vaserber on 10/23/17.
 */

public class Icon extends Component<IconProps, Void> {

    public Icon(@NonNull final IconProps props,
                @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

}
