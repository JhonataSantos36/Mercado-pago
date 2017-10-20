package com.mercadopago.px_tracking.strategies;


import com.mercadopago.px_tracking.model.Event;

import java.util.List;

public interface EventsDatabase {

    void persist(Event event);

    void returnEvents(List<Event> batch);

    Integer getBatchSize();

    List<Event> retrieveBatch();

    void clearExpiredTracks();

    Long getNextTrackTimestamp();

    void clearDatabase();
}
