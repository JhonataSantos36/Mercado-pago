package com.mercadopago.review_and_confirm.components.payment_method;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ContainerComponent<Props, Actions> extends CompactComponent<Props, Actions> {


    public ContainerComponent(final Props props) {
        super(props);
    }

    public ContainerComponent(final Props props, final Actions callBack) {
        super(props, callBack);
    }

    public View render(final Context context, @LayoutRes final int layout) {
        ViewGroup group = (ViewGroup) LayoutInflater.from(context).inflate(layout, null);
        render(group);
        return group;
    }
}
