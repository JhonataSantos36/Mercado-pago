package com.mercadopago.testlib.espresso;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import java.util.Collection;

public final class CurrentActivityFetcher {

    private CurrentActivityFetcher() {
        throw new AssertionError("Can't instantiate a utility class");
    }

    /**
     * Retrieves the current activity
     * @param <T> The expected type of the current activity
     * @return The current activity, casted to the requested type
     */
    public static <T extends Activity> T fetch() {
        final ActivityFetcherRunnable fetcher = new ActivityFetcherRunnable();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(fetcher);
        return (T) fetcher.getCurrentActivity();
    }

    /* default */ static class ActivityFetcherRunnable implements Runnable {
        private Activity currentActivity;

        @Override
        public void run() {
            final Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED);

            if(resumedActivity.iterator().hasNext()){
                currentActivity = resumedActivity.iterator().next();
            }
        }

        /* default */ Activity getCurrentActivity() {
            return currentActivity;
        }
    }
}
