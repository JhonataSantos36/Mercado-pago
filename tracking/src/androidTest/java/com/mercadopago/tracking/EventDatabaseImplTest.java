package com.mercadopago.tracking;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.mercadopago.tracking.mocks.EventMock;
import com.mercadopago.tracking.model.Event;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.strategies.EventsDatabase;
import com.mercadopago.tracking.strategies.EventsDatabaseImpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by marlanti on 8/2/17.
 */

public class EventDatabaseImplTest {

    @Test
    public void testTrackNEventStorage() {
        MockedMPTrackingContext trackingContext = new MockedMPTrackingContext();
        trackingContext.clearDatabase();

        int N = 5;

        ScreenViewEvent event = EventMock.getScreenViewEvent();

        for (int i = 0; i < N; i++) {
            trackingContext.trackEvent(event);
        }

        int storedTracksAmount = trackingContext.getBatchSize();

        assertEquals(storedTracksAmount, N);
    }

    @Test
    public void testClearExpiredTracks() {

        MockedMPTrackingContext trackingContext = new MockedMPTrackingContext();

        trackingContext.clearDatabase();

        int N = 5;

        ScreenViewEvent expiredEvent = EventMock.getExpiredScreenViewEvent();

        for (int i = 0; i < N; i++) {
            trackingContext.trackEvent(expiredEvent);
        }

        trackingContext.clearExpiredTracks();

        int storedTracksAmount = trackingContext.getBatchSize();

        assertEquals(storedTracksAmount, 0);

    }

    private class MockedMPTrackingContext {
        private EventsDatabase database;

        MockedMPTrackingContext() {
            Context appContext = InstrumentationRegistry.getTargetContext();
            database = new EventsDatabaseImpl(appContext);
        }

        public void clearDatabase() {
            database.clearDatabase();
        }

        public void trackEvent(Event event) {
            database.persist(event);
        }

        public void clearExpiredTracks() {
            database.clearExpiredTracks();
        }

        public Integer getBatchSize() {
            return database.getBatchSize();
        }

    }

}
