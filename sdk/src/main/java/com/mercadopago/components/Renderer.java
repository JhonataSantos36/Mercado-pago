package com.mercadopago.components;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mercadopago.util.TextUtil;


/**
 * Created by vaserber on 10/20/17.
 */

public abstract class Renderer<T extends Component> {

    private T component;
    private Context context;

    public void setComponent(@NonNull final T component) {
        this.component = component;
    }

    public void setContext(@NonNull final Context context) {
        this.context = context;
    }

    public View render() {
        return render(null);
    }

    public View render(@Nullable final ViewGroup parent) {
        final View view = render(component, context, parent);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                component.viewAttachedToWindow();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                component.viewDetachedFromWindow();
            }
        });
        return view;
    }

    protected abstract View render(@NonNull final T component,
                                    @NonNull final Context context,
                                    @Nullable final ViewGroup parent);

    protected View inflate(@LayoutRes int layout, @Nullable final ViewGroup parent) {
        return LayoutInflater.from(context).inflate(layout, parent);
    }

    public void wrapHeight(@NonNull final ViewGroup viewGroup) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        viewGroup.setLayoutParams(params);
    }

    public void stretchHeight(@NonNull final ViewGroup viewGroup) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );
        viewGroup.setLayoutParams(params);
    }

    protected void setText(@NonNull final TextView view, @StringRes final int resource) {
        try {
            String text = context.getResources().getString(resource);
            if (text.isEmpty()) {
                view.setVisibility(View.GONE);
            } else {
                view.setText(text);
            }
        } catch (final Resources.NotFoundException ex) {
            //Todo: add to tracker
            view.setVisibility(View.GONE);
        }
    }

    protected void setText(@NonNull final TextView view, String text) {
        if(TextUtil.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
        }
    }
}
