package com.mercadopago.components;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mercadopago.util.TextUtil;


/**
 * Created by vaserber on 10/20/17.
 */

public abstract class Renderer<T extends Component> {

    protected T component;
    protected Context context;

    public void setComponent(@NonNull final T component) {
        this.component = component;
    }

    public void setContext(@NonNull final Context context) {
        this.context = context;
    }

    public abstract View render();

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
