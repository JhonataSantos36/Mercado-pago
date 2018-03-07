package com.mercadopago.review_and_confirm.components.payment_method;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    public abstract View render(@Nonnull final ViewGroup parent);

    @Nullable
    protected Actions getActions() {
        return actions;
    }

    @Nonnull
    public static View inflate(@Nonnull final ViewGroup parent, @LayoutRes final int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    @Nonnull
    public static View compose(@Nonnull final ViewGroup container, @Nonnull final View... children) {
        for (View child : children) {
            container.addView(child);
        }
        return container;
    }
}
