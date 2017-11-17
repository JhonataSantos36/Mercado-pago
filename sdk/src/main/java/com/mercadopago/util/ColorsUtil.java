package com.mercadopago.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.mercadopago.customviews.MPTextView;
import com.mercadopago.preferences.DecorationPreference;

/**
 * Created by mreverter on 2/6/16.
 */
public class ColorsUtil {

    public static int darker(int color) {

        double factor = 0.75;

        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    public static int lighter(int color) {

        double factor = 0.25;

        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a,
                Math.max((int) (r + ((255 - r) * factor)), 0),
                Math.max((int) (g + ((255 - g) * factor)), 0),
                Math.max((int) (b + ((255 - b) * factor)), 0));
    }

    public static void decorateLowResToolbar(Toolbar toolbar, MPTextView title,
                                             DecorationPreference decorationPreference,
                                             ActionBar actionBar,
                                             Context context) {
        toolbar.setBackgroundColor(decorationPreference.getBaseColor());
        if (decorationPreference.isDarkFontEnabled()) {
            decorateDarkFontUpArrow(toolbar, decorationPreference, actionBar, context);
            title.setTextColor(decorationPreference.getDarkFontColor(context));
        }
    }

    public static void decorateNormalToolbar(Toolbar toolbar,
                                             DecorationPreference decorationPreference,
                                             AppBarLayout appBarLayout,
                                             CollapsingToolbarLayout collapsingToolbarLayout,
                                             ActionBar actionBar,
                                             Context context) {
        toolbar.setBackgroundColor(decorationPreference.getLighterColor());
        appBarLayout.setBackgroundColor(decorationPreference.getLighterColor());
        collapsingToolbarLayout.setContentScrimColor(decorationPreference.getLighterColor());
        if (decorationPreference.isDarkFontEnabled()) {
            decorateDarkFontUpArrow(toolbar, decorationPreference, actionBar, context);
            int darkFont = decorationPreference.getDarkFontColor(context);
            collapsingToolbarLayout.setExpandedTitleColor(darkFont);
            collapsingToolbarLayout.setCollapsedTitleTextColor(darkFont);
        }
    }

    public static void decorateTransparentToolbar(Toolbar toolbar, MPTextView title,
                                                  DecorationPreference decorationPreference,
                                                  ActionBar actionBar,
                                                  Context context) {
        if (decorationPreference.isDarkFontEnabled()) {
            decorateDarkFontUpArrow(toolbar, decorationPreference,
                    actionBar, context);
            title.setTextColor(decorationPreference.getDarkFontColor(context));
        }
    }

    public static void decorateTransparentToolbar(Toolbar toolbar, DecorationPreference decorationPreference, ActionBar actionBar, Context context) {
        if (decorationPreference.isDarkFontEnabled()) {
            decorateDarkFontUpArrow(toolbar, decorationPreference,
                    actionBar, context);
        }
    }

    private static void decorateDarkFontUpArrow(Toolbar toolbar,
                                                DecorationPreference decorationPreference,
                                                ActionBar actionBar,
                                                Context context) {
        int darkFont = decorationPreference.getDarkFontColor(context);
        Drawable upArrow = toolbar.getNavigationIcon();
        if (upArrow != null && actionBar != null) {
            upArrow.setColorFilter(darkFont, PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
    }

    public static void decorateTextView(DecorationPreference decorationPreference,
                                        MPTextView textView,
                                        Context context) {
        if (decorationPreference.isDarkFontEnabled()) {
            textView.setTextColor(decorationPreference.getDarkFontColor(context));
        }
    }

    @TargetApi(value = 21)
    public static void tintStatusBar(Activity activity, Integer color) {
        activity.getWindow().setStatusBarColor(color);
    }
}
