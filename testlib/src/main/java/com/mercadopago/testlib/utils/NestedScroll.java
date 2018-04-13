package com.mercadopago.testlib.utils;

import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by mlombardi on 5/9/17.
 */

public final class NestedScroll {

    private NestedScroll() {
        throw new AssertionError("Can't instantiate a utility class");
    }

    public static ViewAction nestedScrollTo() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDescendantOfA(isAssignableFrom(NestedScrollView.class)), withEffectiveVisibility(
                        ViewMatchers.Visibility.VISIBLE));
            }

            @Override
            public String getDescription() {
                return "Scrolling to view inside NestedScrollView";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                try {
                    final NestedScrollView nestedScrollView = (NestedScrollView)
                            findFirstParentLayoutOfClass(view, NestedScrollView.class);
                    if (nestedScrollView != null) {
                        final CoordinatorLayout coordinatorLayout =
                                (CoordinatorLayout) findFirstParentLayoutOfClass(view, CoordinatorLayout.class);
                        if (coordinatorLayout != null) {
                            final CollapsingToolbarLayout collapsingToolbarLayout =
                                    findCollapsingToolbarLayoutChildIn(coordinatorLayout);
                            if (collapsingToolbarLayout != null) {
                                final int toolbarHeight = collapsingToolbarLayout.getHeight();
                                nestedScrollView.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                                nestedScrollView.dispatchNestedPreScroll(0, toolbarHeight, null, null);
                            }
                        }
                        nestedScrollView.scrollTo(0, view.getTop());
                    } else {
                        throw new Exception("Unable to find NestedScrollView parent.");
                    }
                } catch (final Exception e) {
                    throw new PerformException.Builder()
                            .withActionDescription(this.getDescription())
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(e)
                            .build();
                }
                uiController.loopMainThreadUntilIdle();
            }
        };
    }

    @Nullable
    private static View findFirstParentLayoutOfClass(final View view, final Class<? extends View> parentClass) {
        ViewParent parent = new FrameLayout(view.getContext());
        ViewParent incrementView = null;
        int i = 0;
        while (parent != null && parent.getClass() != parentClass) {
            if (i == 0) {
                parent = findParent(view);
            } else {
                parent = findParent(incrementView);
            }
            incrementView = parent;
            i++;
        }
        return (View) parent;
    }

    private static ViewParent findParent(final View view) {
        return view.getParent();
    }

    private static ViewParent findParent(final ViewParent view) {
        return view.getParent();
    }

    @Nullable
    private static CollapsingToolbarLayout findCollapsingToolbarLayoutChildIn(final ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof CollapsingToolbarLayout) {
                return (CollapsingToolbarLayout) child;
            } else if (child instanceof ViewGroup) {
                return findCollapsingToolbarLayoutChildIn((ViewGroup) child);
            }
        }
        return null;
    }
}
