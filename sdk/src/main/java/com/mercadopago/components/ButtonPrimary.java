package com.mercadopago.components;


import com.mercadopago.R;

public class ButtonPrimary extends Button {

    public ButtonPrimary(final Props props, final Actions callBack) {
        super(props, callBack);
    }

    @Override
    public int getButtonViewLayout() {
        return R.layout.mpsdk_view_text_button_blue;
    }
}
