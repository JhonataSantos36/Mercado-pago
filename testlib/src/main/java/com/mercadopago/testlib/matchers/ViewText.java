package com.mercadopago.testlib.matchers;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

public final class ViewText {

    private ViewText() {
        throw new AssertionError("Can't instantiate a utility class");
    }

    public static String getTextFromMatcher(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                final TextView tv = (TextView)view;
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

}
