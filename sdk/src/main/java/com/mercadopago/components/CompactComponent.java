package com.mercadopago.components;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mercadopago.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CompactComponent<Props, Actions> {

    protected Props props;
    private final Actions actions;

    public CompactComponent() {
        this(null, null);
    }

    public CompactComponent(final Props props) {
        this(props, null);
    }

    public CompactComponent(final Props props, final Actions callBack) {
        actions = callBack;
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

    @Nonnull
    public static View compose(@Nonnull final ViewGroup container, @Nonnull final View child) {
        container.addView(child);
        return container;
    }

    @Nonnull
    public static LinearLayout createLinearContainer(final Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    @Nonnull
    public static ScrollView createScrollContainer(final Context context) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.setBackgroundColor(scrollView
                .getContext()
                .getResources()
                .getColor(R.color.mpsdk_white_background));
        return scrollView;
    }
}
