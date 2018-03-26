package com.mercadopago.components;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;

public class ToolbarRenderer extends Renderer<ToolbarComponent> {

    @Override
    @CallSuper
    public View render(final ToolbarComponent component, final Context context, final ViewGroup parent) {
        final View view = inflate(R.layout.mpsdk_toolbar_renderer, parent);
        renderToolbar(view, component, context);
        return view;
    }

    private void renderToolbar(final View view, final ToolbarComponent component, final Context context) {

        final AppCompatActivity activity = (AppCompatActivity) context;
        final Toolbar toolbar = view.findViewById(R.id.toolbar);

        if (!component.props.toolbarVisible) {

            toolbar.setVisibility(View.GONE);

        } else {

            final TextView titleView = view.findViewById(R.id.title);

            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });

            titleView.setText(component.props.toolbarTitle);
        }
    }
}
