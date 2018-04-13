package com.mercadopago.testlib.idlingresource;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingResource;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import java.util.Collection;

public class ActivityIdlingResource implements IdlingResource {

    private final Class<? extends Activity> mAcivity;
    private final String mName;
    private ResourceCallback mResourceCallback;

    public ActivityIdlingResource(@NonNull Class<? extends Activity> activity) {
        mAcivity = activity;
        mName = "activity " + activity.getName() + "(@" + System.identityHashCode(activity) + ")";
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isIdleNow() {
        final boolean isIdle = mAcivity == getActivityInstance().getClass();
        if (!isIdle && mResourceCallback != null) {
            mResourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        mResourceCallback = resourceCallback;
    }

    private Activity getActivityInstance() {
        final Activity[] mActivity = new Activity[1];
        Collection<Activity> resumedActivities =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
        mActivity[0] = resumedActivities.iterator().next();
        return mActivity[0];
    }
}