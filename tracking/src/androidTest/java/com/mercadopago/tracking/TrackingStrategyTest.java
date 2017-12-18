package com.mercadopago.tracking;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.mercadopago.tracking.mocks.MPMockedTrackingService;
import com.mercadopago.tracking.mocks.TrackingStaticMock;
import com.mercadopago.tracking.model.Event;
import com.mercadopago.tracking.model.EventTrackIntent;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.strategies.RealTimeTrackingStrategy;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TrackingStrategyTest {

    private static final String MOCKED_PK = "TEST-PK";
    @Test
    public void whenEventsAreFromTheSameFlowThenThereShouldBeOneIntent() {

        //Given a list of events and a Strategy
        List<Event> eventList = new ArrayList<>();
        ScreenViewEvent screenViewEvent1 = new ScreenViewEvent.Builder()
                .setScreenId("/some-id")
                .setScreenName("Some page")
                .setFlowId("1234")
                .build();
        ScreenViewEvent screenViewEvent2 = new ScreenViewEvent.Builder()
                .setScreenId("/another-id")
                .setScreenName("Another page")
                .setFlowId("1234")
                .build();

        eventList.add(screenViewEvent1);
        eventList.add(screenViewEvent2);

        RealTimeTrackingStrategy strategy = new RealTimeTrackingStrategy(new MPMockedTrackingService());
        strategy.setAppInformation(TrackingStaticMock.getApplicationInformation());
        strategy.setDeviceInfo(TrackingStaticMock.getDeviceInformation());
        strategy.setPublicKey(MOCKED_PK);

        //When group by flow called
        List<EventTrackIntent> eventTrackIntents = strategy.groupEventsByFlow(eventList);

        //Then there should be one intent
        Assert.assertTrue(eventTrackIntents.size() == 1);
    }

    @Test
    public void whenEventsAreFromDifferentFlowsThenThereShouldBeMultipleIntent() {

        //Given a list of events and a Strategy
        List<Event> eventList = new ArrayList<>();
        ScreenViewEvent screenViewEvent1 = new ScreenViewEvent.Builder()
                .setScreenId("/some-id")
                .setScreenName("Some page")
                .setFlowId("1234")
                .build();
        ScreenViewEvent screenViewEvent2 = new ScreenViewEvent.Builder()
                .setScreenId("/another-id")
                .setScreenName("Another page")
                .setFlowId("1234")
                .build();

        ScreenViewEvent screenViewEvent3 = new ScreenViewEvent.Builder()
                .setScreenId("/some-id-from-another-flow")
                .setScreenName("Some page")
                .setFlowId("9876")
                .build();
        ScreenViewEvent screenViewEvent4 = new ScreenViewEvent.Builder()
                .setScreenId("/another-id-from-another-flow")
                .setScreenName("Another page")
                .setFlowId("9876")
                .build();

        eventList.add(screenViewEvent1);
        eventList.add(screenViewEvent2);
        eventList.add(screenViewEvent3);
        eventList.add(screenViewEvent4);

        RealTimeTrackingStrategy strategy = new RealTimeTrackingStrategy(new MPMockedTrackingService());
        strategy.setAppInformation(TrackingStaticMock.getApplicationInformation());
        strategy.setDeviceInfo(TrackingStaticMock.getDeviceInformation());
        strategy.setPublicKey(MOCKED_PK);

        //When group by flow called
        List<EventTrackIntent> eventTrackIntents = strategy.groupEventsByFlow(eventList);

        //Then there should be one intent
        Assert.assertTrue(eventTrackIntents.size() == 2);
    }

    @Test
    public void applicationFlowIdMustMatchEventFlowId() {

        //Given a list of events and a Strategy
        List<Event> eventList = new ArrayList<>();
        ScreenViewEvent screenViewEvent1 = new ScreenViewEvent.Builder()
                .setScreenId("/some-id")
                .setScreenName("Some page")
                .setFlowId("1234")
                .build();
        ScreenViewEvent screenViewEvent2 = new ScreenViewEvent.Builder()
                .setScreenId("/another-id")
                .setScreenName("Another page")
                .setFlowId("1234")
                .build();

        ScreenViewEvent screenViewEvent3 = new ScreenViewEvent.Builder()
                .setScreenId("/some-id-from-another-flow")
                .setScreenName("Some page")
                .setFlowId("9876")
                .build();
        ScreenViewEvent screenViewEvent4 = new ScreenViewEvent.Builder()
                .setScreenId("/another-id-from-another-flow")
                .setScreenName("Another page")
                .setFlowId("9876")
                .build();

        eventList.add(screenViewEvent1);
        eventList.add(screenViewEvent2);
        eventList.add(screenViewEvent3);
        eventList.add(screenViewEvent4);

        RealTimeTrackingStrategy strategy = new RealTimeTrackingStrategy(new MPMockedTrackingService());
        strategy.setAppInformation(TrackingStaticMock.getApplicationInformation());
        strategy.setDeviceInfo(TrackingStaticMock.getDeviceInformation());
        strategy.setPublicKey(MOCKED_PK);

        //When group by flow called
        List<EventTrackIntent> eventTrackIntents = strategy.groupEventsByFlow(eventList);

        //Then app flow id must match events
        EventTrackIntent trackIntent1 = eventTrackIntents.get(0);
        Assert.assertEquals(trackIntent1.getApplication().getFlowId(), trackIntent1.getEvents().get(0).getFlowId());
        Assert.assertEquals(trackIntent1.getApplication().getFlowId(), trackIntent1.getEvents().get(1).getFlowId());

        EventTrackIntent trackIntent2 = eventTrackIntents.get(1);
        Assert.assertEquals(trackIntent2.getApplication().getFlowId(), trackIntent2.getEvents().get(0).getFlowId());
        Assert.assertEquals(trackIntent2.getApplication().getFlowId(), trackIntent2.getEvents().get(1).getFlowId());
    }
}
