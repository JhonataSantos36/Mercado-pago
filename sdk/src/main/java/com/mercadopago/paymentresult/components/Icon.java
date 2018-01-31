package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.IconProps;
import com.mercadopago.util.TextUtils;

/**
 * Created by vaserber on 10/23/17.
 */

public class Icon extends Component<IconProps, Void> {

    public Icon(@NonNull final IconProps props,
                @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public boolean hasIconFromUrl() {
        return !TextUtils.isEmpty(props.iconUrl);
    }

}
