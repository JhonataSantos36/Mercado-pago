package com.mercadopago.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Workaround AppBarLayout.Behavior for https://issuetracker.google.com/66996774
 *
 * See https://gist.github.com/chrisbanes/8391b5adb9ee42180893300850ed02f2 for
 * example usage.
 *
 */
public class FixAppBarLayoutBehavior extends AppBarLayout.Behavior {

    public FixAppBarLayoutBehavior() {
        super();
    }

    public FixAppBarLayoutBehavior(@NonNull final Context context,
                                   @NonNull final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedScroll(@NonNull final CoordinatorLayout coordinatorLayout,
                               @NonNull final AppBarLayout child,
                               @NonNull final View target,
                               final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed,
                               final int type) {

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, type);
        stopNestedScrollIfNeeded(dyUnconsumed, child, target, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull final CoordinatorLayout coordinatorLayout,
                                  @NonNull final AppBarLayout child,
                                  @NonNull final View target, final int dx, final int dy,
                                  final int[] consumed, final int type) {

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        stopNestedScrollIfNeeded(dy, child, target, type);
    }

    private void stopNestedScrollIfNeeded(final int dy,
                                          @NonNull final AppBarLayout child,
                                          @NonNull final View target, final int type) {

        if (type == ViewCompat.TYPE_NON_TOUCH) {
            final int currOffset = getTopAndBottomOffset();
            if ((dy < 0 && currOffset == 0)
                    || (dy > 0 && currOffset == -child.getTotalScrollRange())) {
                ViewCompat.stopNestedScroll(target, ViewCompat.TYPE_NON_TOUCH);
            }
        }
    }
}