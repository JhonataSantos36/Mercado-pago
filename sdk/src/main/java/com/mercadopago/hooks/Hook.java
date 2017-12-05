package com.mercadopago.hooks;

import com.mercadopago.components.Component;

public interface Hook {

    Component<HookComponent.Props> createComponent();

    boolean isEnabled();
}