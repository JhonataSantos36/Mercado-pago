package com.mercadopago.components;

import android.content.Context;
import android.view.View;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mercadopago.util.TextUtils;


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

    //TODO: move to component
    @Deprecated
    public void wrapHeight(@NonNull final ViewGroup viewGroup) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        viewGroup.setLayoutParams(params);
    }

    //TODO: move to component
    @Deprecated
    public void stretchHeight(@NonNull final ViewGroup viewGroup) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );
        viewGroup.setLayoutParams(params);
    }

    //TODO: move to Text component
    @Deprecated
    protected void setText(@NonNull final TextView view, final int id) {
        try {
            String text = context.getResources().getString(id);
            if (text.isEmpty()) {
                view.setVisibility(View.GONE);
            } else {
                view.setText(text);
            }
        } catch (Resources.NotFoundException ex) {
            //Todo: add to tracker
            view.setVisibility(View.GONE);
        }
    }

    //TODO: move to Text component
    @Deprecated
    protected void setText(@NonNull final TextView view, String text) {
        if(TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
        }
    }
}