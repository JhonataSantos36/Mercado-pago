package com.mercadopago.components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaserber on 10/20/17.
 */

public class RendererFactory {

    private static final Map<Class, Class> rendererRegistry = new HashMap<>();


//    Create renderers and set their components and context here. For example:

    public static void register(@NonNull final Class component, @NonNull final Class renderer) {
        rendererRegistry.put(component, renderer);
    }

    public static Renderer create(@NonNull final Context context, @NonNull final Component component) {
        Renderer renderer = null;
        try {
            renderer = (Renderer) rendererRegistry.get(component.getClass()).newInstance();
            renderer.setComponent(component);
            renderer.setContext(context);
        } catch (Exception e) {
            Log.e("error", e.getMessage(), e);
        }
        return renderer;
    }
}