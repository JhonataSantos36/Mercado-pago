package com.mercadopago.components;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * Created by vaserber on 10/20/17.
 */

public class ComponentManager<T> implements ActionDispatcher, MutatorPropsListener<T> {

    private Activity activity;
    private Component root;
    private ActionsListener actionsListener;
    private Mutator mutator;
    private Renderer renderer;

    public ComponentManager(@NonNull final Activity activity) {
        this.activity = activity;
    }

    public void setComponent(@NonNull final Component component) {
        root = component;
        renderer = RendererFactory.create(activity, root);
    }

    private void render() {
        if (renderer != null) {
            activity.setContentView(renderer.render());
        }
    }

    public void setActionsListener(@NonNull final ActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    @Override
    public void dispatch(@NonNull final Action action) {
        if (actionsListener != null) {
            actionsListener.onAction(action);
        }
    }

    public void setMutator(@NonNull final Mutator mutator) {
        this.mutator = mutator;
        this.mutator.setPropsListener(this);
    }

    @Override
    public void onProps(@NonNull final T props) {
        root.setProps(props);
        render();
    }
}