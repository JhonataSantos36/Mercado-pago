package com.mercadopago.core;

import android.support.annotation.NonNull;

import com.mercadopago.components.Action;
import com.mercadopago.components.ActionDispatcher;
import com.mercadopago.components.ActionsListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nfortuna on 12/14/17.
 */

public class CheckoutActionDispatcher implements ActionDispatcher {

    private List<ActionsListener> listeners = new ArrayList<>();

    @Override
    public void dispatch(@NonNull final Action action) {
        for (final ActionsListener listener : listeners) {
            listener.onAction(action);
        }
    }
}