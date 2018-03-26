package com.mercadopago.components;


import com.mercadopago.R;

public class ButtonLink extends Button {

    public ButtonLink(final Props props, final Actions callBack) {
        super(props, callBack);
    }

    @Override
    public int getButtonViewLayout() {
        return R.layout.mpsdk_view_text_button_link;
    }
}
