package com.mercadopago.paymentresult.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.Component;
import com.mercadopago.paymentresult.props.HeaderProps;
import com.mercadopago.paymentresult.props.IconProps;

/**
 * Created by vaserber on 10/20/17.
 */

public class Header extends Component<HeaderProps> {

    public Header(@NonNull final HeaderProps props,
                  @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public Icon getIconComponent() {

        final IconProps iconProps = new IconProps.Builder()
                .setIconImage(props.iconImage)
                .setBadgeImage(props.badgeImage)
                .build();

        return new Icon(iconProps, getDispatcher());
    }
}