package com.mercadopago.hooks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

public abstract class HookRenderer<T extends HookComponent> extends Renderer<T> {

    @Override
    public View render(final T component, final Context context, final ViewGroup parent) {
        final ViewGroup view = (ViewGroup) inflate(R.layout.mpsdk_hooks_layout, parent);

        RendererFactory.create(context, component.getToolbarComponent()).render(view);

        final View contents = renderContents(component, context);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1f);
        contents.setLayoutParams(params);
        view.addView(contents);
        return view;
    }

    public abstract View renderContents(final T component, final Context context);
}
