package com.mercadopago.utils;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by vaserber on 7/12/16.
 */
public class ViewUtils {

    public static int getBackgroundColor(View view) {
        ColorDrawable drawable = (ColorDrawable) view.getBackground();
        if (Build.VERSION.SDK_INT >= 11) {
            return drawable.getColor();
        }
        try {
            Field field = drawable.getClass().getDeclaredField("mState");
            field.setAccessible(true);
            Object object = field.get(drawable);
            field = object.getClass().getDeclaredField("mUseColor");
            field.setAccessible(true);
            return field.getInt(object);
        } catch (Exception e) {
        }
        return 0;
    }

    public static boolean hasItems(RecyclerView recyclerView) {
        return recyclerView.getAdapter().getItemCount() != 0;
    }
    public static boolean hasItems(RecyclerView recyclerView, int count) {
        return recyclerView.getAdapter().getItemCount() == count;
    }
}
