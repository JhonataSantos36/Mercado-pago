package com.mercadopago.components;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * Created by vaserber on 10/20/17.
 */

public class ComponentManager<T> implements ActionDispatcher, MutatorPropsListener<T> {

    private Activity activity;
    private Component root;
    private ActionsListener actionsListener;
    private Renderer renderer;

    public ComponentManager(@NonNull final Activity activity) {
        this.activity = activity;
    }

    public void setComponent(@NonNull final Component component) {
        root = component;
        renderer = RendererFactory.create(activity, root);
    }

    private void render() {
        if (renderer != null && !activity.isFinishing()) {
            activity.setContentView(renderer.render(null));
        }
    }

    public void render(final Component component) {
        if (component != null) {
            setComponent(component);
            render();
        }
    }

    public void render(@NonNull final Component component, @NonNull final ViewGroup parent) {
        setComponent(component);
        if (renderer != null && !activity.isFinishing()) {
            renderer.render(parent);
        }
    }

    public void setActionsListener(@NonNull final ActionsListener actionsListener) {
        this.actionsListener = actionsListener;
    }

    @Override
    public void dispatch(@NonNull final Action action) {
        if (action instanceof PropsUpdatedAction) {
            render();
        } else if (actionsListener != null) {
            actionsListener.onAction(action);
        }
    }

    @Override
    public void onProps(@NonNull final T props) {
        root.setProps(props);
        render();
    }
}
