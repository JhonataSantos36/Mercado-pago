package com.mercadopago.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mercadopago.R;
import com.mercadopago.views.MPEditText;

public class LayoutUtil {

    public static void hideKeyboard(Activity activity) {

        try {
            MPEditText editText = (MPEditText) activity.getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        catch (Exception ex) {
        }
    }

    public static void showProgressLayout(Activity activity) {
        showLayout(activity, true, false, false);
    }

    public static void showRegularLayout(Activity activity) {
        showLayout(activity, false, true, false);
    }

    public static void showRefreshLayout(Activity activity) {
        showLayout(activity, false, false, true);
    }

    private static void showLayout(Activity activity, final boolean showProgress, final boolean showLayout, final boolean showRefresh) {

        final View form = activity.findViewById(R.id.mpsdkRegularLayout);
        final View progress = activity.findViewById(R.id.mpsdkProgressLayout);
        final View refresh = activity.findViewById(R.id.mpsdkRefreshLayout);

        if(progress != null) {
            progress.setVisibility(showRefresh || showLayout ? View.GONE : View.VISIBLE);
        }

        if(form != null) {
            form.setVisibility(showRefresh || showProgress ? View.GONE : View.VISIBLE);
        }

        if (refresh != null) {
            refresh.setVisibility(showRefresh ? View.VISIBLE : View.GONE);
        }
    }
}
