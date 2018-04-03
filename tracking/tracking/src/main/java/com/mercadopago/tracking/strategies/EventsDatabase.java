package com.mercadopago.tracking.strategies;


import com.mercadopago.tracking.model.Event;

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
