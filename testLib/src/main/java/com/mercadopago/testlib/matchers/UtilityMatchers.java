package com.mercadopago.testlib.matchers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkArgument;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mlombardi on 15/8/17.
 */

public final class UtilityMatchers {

    private UtilityMatchers() {
        throw new AssertionError("Can't instantiate a utility class");
    }

    public static <T> Matcher<T> isFirstMatch(final Matcher<T> matcher) {
        return new BaseMatcher<T>() {
            boolean isFirst = true;

            @Override
            public boolean matches(final Object item) {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false;
                    return true;
                }

                return false;
            }

            @Override
            public void describeMismatch(final Object item, final Description description) {
                description.appendText("should return first matching item");
                super.describeMismatch(item, description);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("should return first matching item");
            }
        };
    }

    public static Matcher<View> withBackgroundColor(final int color) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View item) {
                if (item != null) {
                    int bgColor = Color.TRANSPARENT;
                    final Drawable background = item.getBackground();
                    if (background instanceof ColorDrawable) {
                        bgColor = ((ColorDrawable) background).getColor();
                    }
                    return bgColor == color;
                }
                return false;
            }

            @Override
            protected void describeMismatchSafely(final View item, final Description mismatchDescription) {
                mismatchDescription.appendText("Match View contains background color");
                super.describeMismatchSafely(item, mismatchDescription);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Match View contains background color");
            }
        };
    }

    public static Matcher<View> withItemTextInRecyclerView(final String itemText) {
        checkArgument(!itemText.isEmpty(),"cannot be empty");
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(final View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("is descendant of a RecyclerView with text" + itemText);
            }
        };
    }
}