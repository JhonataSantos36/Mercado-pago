package com.mercadopago.tracking.listeners;

import android.support.annotation.NonNull;

/**
 * Created by vaserber on 6/5/17.
 */

public interface TracksListener<T> {

    void onScreenLaunched(@NonNull final String screenName);

    @Deprecated
    void onEvent(@NonNull final T event);

    class Empty<T> implements TracksListener<T> {
        @Override
        public void onScreenLaunched(@NonNull String screenName) {
            //Do nothing
        }

        @Override
        public void onEvent(@NonNull Object event) {
            //Do nothing
        }
    }
}
