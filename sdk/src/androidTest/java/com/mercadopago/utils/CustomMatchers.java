package com.mercadopago.utils;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by mreverter on 11/7/16.
 */
public class CustomMatchers {
    public static Matcher<View> withAnyChildText(final String text) {
        return new BoundedMatcher<View, ViewGroup>(LinearLayout.class) {
            @Override
            public boolean matchesSafely(ViewGroup view) {
                return anyChildMatches(view, text);
            }

            private boolean anyChildMatches(ViewGroup view, String text) {
                for(int i=0; i < view.getChildCount(); i++) {
                    View child = view.getChildAt(i);
                    if (child != null && child instanceof TextView) {
                        if(((TextView) child).getText().toString().equals(text)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with child text: ");
            }
        };
    }
}
