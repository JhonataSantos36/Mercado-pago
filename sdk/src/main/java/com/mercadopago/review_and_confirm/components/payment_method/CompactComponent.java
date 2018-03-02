package com.mercadopago.review_and_confirm.components.payment_method;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lbais on 27/2/18.
 */

public abstract class CompactComponent<Props, Actions> {

    protected Props props;
    private Actions actions;

    public CompactComponent(final Props props) {
        this(props, null);
    }

    public CompactComponent(final Props props, final Actions callBack) {
        this.actions = callBack;
        this.props = props;
    }

    public View setProps(final Props props, final ViewGroup parent) {
        this.props = props;
        return render(parent);
    }

    protected View compose(final ViewGroup container, final View... children) {
        for (View child : children) {
            container.addView(child);
        }
        return container;
    }

    public abstract View render(final ViewGroup parent);

    protected View inflate(final ViewGroup parent, @LayoutRes final int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    protected Actions getActions() {
        return actions;
    }
}
