package com.mercadopago.px_tracking.listeners;

import java.util.Map;

/**
 * Created by vaserber on 6/5/17.
 */

public interface TracksListener {

    void onScreenLaunched(String screenName);

    @Deprecated
    void onEventPerformed(Map<String, String> event);
}
