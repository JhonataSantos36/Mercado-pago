package com.mercadopago.components;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/20/17.
 */

public class Component<T> {

    public T props;
    private ActionDispatcher dispatcher;

    public Component(@NonNull final T props) {
        this(props, null);
    }

    public Component(@NonNull final T props, @NonNull final ActionDispatcher dispatcher) {
        this.props = props;
        this.dispatcher = dispatcher;
        setProps(props);
    }

    public ActionDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(ActionDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void setProps(@NonNull final T props) {
        this.props = props;
    }
}