package com.mercadopago.hooks;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.components.BackAction;
import com.mercadopago.components.Renderer;

public abstract class HookRenderer extends Renderer<HookComponent> {

    @Override
    @CallSuper
    public View render() {
        final View view = LayoutInflater.from(context).inflate(R.layout.mpsdk_hook_renderer, null);
        renderToolbar(view);
        return view;
    }

    private void renderToolbar(final View view) {

        final AppCompatActivity activity = (AppCompatActivity) context;
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (!component.props.toolbarVisible) {

            toolbar.setVisibility(View.GONE);

        } else {

            final TextView titleView = (TextView) view.findViewById(R.id.title);

            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    component.getDispatcher().dispatch(new BackAction());
                }
            });

            decorate(activity, toolbar);
            decorateFont(titleView);

            titleView.setText(component.props.toolbarTitle);
        }
    }

    private void decorate(final AppCompatActivity activity, final Toolbar toolbar) {
        if (toolbar != null) {
            if (isCustomColorSet()) {
                toolbar.setBackgroundColor(getCustomBaseColor());
            }
            decorateUpArrow(activity, toolbar);
        }
    }

    private void decorateUpArrow(final AppCompatActivity activity, final Toolbar toolbar) {
        if (isDarkFontEnabled()) {
            int darkFont = getDarkFontColor();
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null && activity.getSupportActionBar() != null) {
                upArrow.setColorFilter(darkFont, PorterDuff.Mode.SRC_ATOP);
                activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

    protected void decorateFont(final TextView textView) {
        if (textView != null && isDarkFontEnabled()) {
            textView.setTextColor(getDarkFontColor());
        }
    }

    private boolean isCustomColorSet() {
        return component.props.decorationPreference.hasColors();
    }

    private boolean isDarkFontEnabled() {
        return component.props.decorationPreference.isDarkFontEnabled();
    }

    private int getCustomBaseColor() {
        return component.props.decorationPreference.getBaseColor();
    }

    private int getDarkFontColor() {
        return component.props.decorationPreference.getDarkFontColor(context);
    }
}
