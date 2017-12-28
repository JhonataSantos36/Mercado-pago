package com.mercadopago.hooks;

import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mercadopago.R;
import com.mercadopago.components.Renderer;
import com.mercadopago.components.RendererFactory;

public abstract class HookRenderer extends Renderer<HookComponent> {

    @Override
    @CallSuper
    public View render() {
        final ViewGroup view = (ViewGroup) LayoutInflater.from(context)
                .inflate(R.layout.mpsdk_hooks_layout, null);
        view.addView(RendererFactory.create(context, component.getToolbarComponent()).render());

        final View contents = renderContents();
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1f);
        contents.setLayoutParams(params);
        view.addView(contents);
        return view;
    }

    public abstract View renderContents();
}
