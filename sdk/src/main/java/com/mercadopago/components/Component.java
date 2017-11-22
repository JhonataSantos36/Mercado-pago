package com.mercadopago.components;

import android.support.annotation.NonNull;

/**
 * Created by vaserber on 10/20/17.
 */

public abstract class Component<T> {

    public T props;
    private final ActionDispatcher dispatcher;

    public Component(@NonNull final T props, @NonNull final ActionDispatcher dispatcher) {
        this.props = props;
        this.dispatcher = dispatcher;
        //TODO: move lifecycle methods calls to component manager
        setProps(props);
    }

    public ActionDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setProps(@NonNull final T props) {
        this.props = props;
    }
}