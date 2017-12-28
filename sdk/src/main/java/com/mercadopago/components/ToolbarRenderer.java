package com.mercadopago.components;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.core.CheckoutStore;
import com.mercadopago.preferences.DecorationPreference;

public class ToolbarRenderer extends Renderer<ToolbarComponent> {

    private DecorationPreference decorationPreference = CheckoutStore.getInstance().getDecorationPreference();

    @Override
    @CallSuper
    public View render() {
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.mpsdk_toolbar_renderer, null);
        renderToolbar(view);
        return view;
    }

    private void renderToolbar(final View view) {

        final AppCompatActivity activity = (AppCompatActivity) context;
        final Toolbar toolbar = view.findViewById(R.id.toolbar);

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
                    activity.onBackPressed();
                }
            });

            decorate(activity, toolbar);
            decorateFont(titleView);

            titleView.setText(component.props.toolbarTitle);
        }
    }

    private void decorate(final AppCompatActivity activity, final Toolbar toolbar) {
        if (toolbar != null) {
            if (decorationPreference.hasColors()) {
                toolbar.setBackgroundColor(decorationPreference.getBaseColor());
            }
            decorateUpArrow(activity, toolbar);
        }
    }

    private void decorateUpArrow(final AppCompatActivity activity, final Toolbar toolbar) {
        if (decorationPreference.isDarkFontEnabled()) {
            int darkFont = decorationPreference.getDarkFontColor(context);
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null && activity.getSupportActionBar() != null) {
                upArrow.setColorFilter(darkFont, PorterDuff.Mode.SRC_ATOP);
                activity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }
    }

    protected void decorateFont(final TextView textView) {
        if (textView != null && decorationPreference.isDarkFontEnabled()) {
            textView.setTextColor(decorationPreference.getDarkFontColor(context));
        }
    }
}
