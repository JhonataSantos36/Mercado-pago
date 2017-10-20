package com.mercadopago.tracking.mocks;

import android.support.test.InstrumentationRegistry;

import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.utils.JsonConverter;

/**
 * Created by marlanti on 8/7/17.
 */

public class EventMock {

    public static ScreenViewEvent getScreenViewEvent(){
        try {
            String json = MockUtils.getFile(InstrumentationRegistry.getContext(), "mocks/screen_view_event.json");
            return JsonConverter.getInstance().fromJson(json, ScreenViewEvent.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public static ScreenViewEvent getExpiredScreenViewEvent(){
        try {
            String json = MockUtils.getFile(InstrumentationRegistry.getContext(), "mocks/expired_screen_view_event.json");
            return JsonConverter.getInstance().fromJson(json, ScreenViewEvent.class);
        } catch (Exception ex) {
            return null;
        }
    }
}
