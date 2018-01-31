package com.mercadopago.tracking.tracker;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.tracking.listeners.TracksListener;
import com.mercadopago.tracking.model.ActionEvent;
import com.mercadopago.tracking.model.AppInformation;
import com.mercadopago.tracking.model.DeviceInfo;
import com.mercadopago.tracking.model.Event;
import com.mercadopago.tracking.model.PaymentIntent;
import com.mercadopago.tracking.model.ScreenViewEvent;
import com.mercadopago.tracking.model.TrackingIntent;
import com.mercadopago.tracking.services.MPTrackingService;
import com.mercadopago.tracking.services.MPTrackingServiceImpl;
import com.mercadopago.tracking.strategies.BatchTrackingStrategy;
import com.mercadopago.tracking.strategies.ConnectivityCheckerImpl;
import com.mercadopago.tracking.strategies.EventsDatabaseImpl;
import com.mercadopago.tracking.strategies.ForcedStrategy;
import com.mercadopago.tracking.strategies.NoOpStrategy;
import com.mercadopago.tracking.strategies.RealTimeTrackingStrategy;
import com.mercadopago.tracking.strategies.TrackingStrategy;
import com.mercadopago.tracking.utils.JsonConverter;
import com.mercadopago.tracking.utils.TrackingUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

/**
 * Created by vaserber on 6/5/17.
 */

public class MPTracker {

    private static MPTracker mMPTrackerInstance;
    private EventsDatabaseImpl database;

    private TracksListener mTracksListener;

    private MPTrackingService mMPTrackingService;

    private String mPublicKey;
    private String mSdkVersion;
    private String mSiteId;
    private Context mContext;

    private static final String SDK_PLATFORM = "Android";
    private static final String SDK_TYPE = "native";

    private static final String DEFAULT_SITE = "";
    private static final String DEFAULT_FLAVOUR = "3";

    private Boolean trackerInitialized = false;

    private TrackingStrategy trackingStrategy;
    private Event mEvent;

    protected MPTracker() {
    }

    synchronized public static MPTracker getInstance() {
        if (mMPTrackerInstance == null) {
            mMPTrackerInstance = new MPTracker();
        }
        return mMPTrackerInstance;
    }

    private void initializeMPTrackingService() {
        if (mMPTrackingService == null) {
            mMPTrackingService = new MPTrackingServiceImpl();
        }
    }

    public void setMPTrackingService(MPTrackingService trackingService) {
        mMPTrackingService = trackingService;
    }

    public void setTracksListener(TracksListener tracksListener) {
        this.mTracksListener = tracksListener;
    }

    private void trackScreenLaunchedListener(String screenName) {
        if (this.mTracksListener != null) {
            this.mTracksListener.onScreenLaunched(screenName);
        }
    }

    private void trackEventPerformedListener(Map<String, String> eventMap) {
        if (this.mTracksListener != null) {
            this.mTracksListener.onEventPerformed(eventMap);
        }
    }

    /**
     * @param paymentId The payment id of a payment method off. Cannot be {@code null}.
     * @param typeId    The payment type id. It has to be a card type.
     */
    public PaymentIntent trackPayment(Long paymentId, String typeId) {

        PaymentIntent paymentIntent = null;

        if (trackerInitialized) {
            paymentIntent = new PaymentIntent(mPublicKey, paymentId.toString(), DEFAULT_FLAVOUR, SDK_PLATFORM, SDK_TYPE, mSdkVersion, mSiteId);
            initializeMPTrackingService();
            mMPTrackingService.trackPaymentId(paymentIntent, mContext);
        }
        return paymentIntent;
    }

    /**
     * @param token The card token id of a payment. Cannot be {@code null}.
     */
    public TrackingIntent trackToken(String token) {
        TrackingIntent trackingIntent = null;
        if (trackerInitialized && !isEmpty(token)) {
            trackingIntent = new TrackingIntent(mPublicKey, token, DEFAULT_FLAVOUR, SDK_PLATFORM, SDK_TYPE, mSdkVersion, mSiteId);
            initializeMPTrackingService();
            mMPTrackingService.trackToken(trackingIntent, mContext);
        }
        return trackingIntent;
    }

    /**
     * This method tracks a list of events in one request
     *
     * @param appInformation Info about this application and SDK integration
     * @param deviceInfo     Info about the device that is using the app
     * @param event          Event to track
     * @param context        Application context
     */
    public void trackEvent(final String publicKey,
                           final AppInformation appInformation,
                           final DeviceInfo deviceInfo,
                           final Event event,
                           final Context context) {
        trackEvent(publicKey, appInformation, deviceInfo, event, context, TrackingUtil.NOOP_STRATEGY);
    }

    /**
     * This method tracks a list of events in one request
     *
     * @param appInformation Info about this application and SDK integration
     * @param deviceInfo     Info about the device that is using the app
     * @param event          Event to track
     * @param context        Application context
     */
    public void trackEvent(final String publicKey,
                           final AppInformation appInformation,
                           final DeviceInfo deviceInfo,
                           final Event event,
                           final Context context,
                           final String trackingStrategy) {

        initializeMPTrackingService();

        mEvent = event;
        mContext = context;

        initializeDatabase();

        setTrackingStrategy(context, event, trackingStrategy);

        if (this.trackingStrategy != null) {
            this.trackingStrategy.setPublicKey(publicKey);
            this.trackingStrategy.setAppInformation(appInformation);
            this.trackingStrategy.setDeviceInfo(deviceInfo);
            this.trackingStrategy.trackEvent(event, context);
        }

        if (!isRealTimeStrategy(trackingStrategy)) {
            database.persist(event);
        }

        if (event.getType().equals(Event.TYPE_ACTION)) {
            ActionEvent actionEvent = (ActionEvent) event;
            Map<String, String> eventMap = createEventMap(actionEvent);
            trackEventPerformedListener(eventMap);
        } else if (event.getType().equals(Event.TYPE_SCREEN_VIEW)) {
            ScreenViewEvent screenViewEvent = (ScreenViewEvent) event;
            trackScreenLaunchedListener(screenViewEvent.getScreenName());
        }
    }

    private void initializeDatabase() {
        if (database == null) {
            this.database = new EventsDatabaseImpl(mContext);
        }
    }

    private Map<String, String> createEventMap(ActionEvent actionEvent) {
        Map<String, String> eventMap = new HashMap<>();

        String eventJson = JsonConverter.getInstance().toJson(actionEvent);
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> actionEventDataMap = new Gson().fromJson(eventJson, type);

        eventMap.putAll(actionEventDataMap);

        return eventMap;
    }


    /**
     * @param publicKey  The public key of the merchant. Cannot be {@code null}.
     * @param siteId     The site that comes in the preference. Cannot be {@code null}.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}.
     * @param context    Reference to Android Context. Cannot be {@code null}.
     */
    public void initTracker(String publicKey, String siteId, String sdkVersion, Context context) {
        if (!isTrackerInitialized()) {
            if (areInitParametersValid(publicKey, siteId, sdkVersion, context)) {
                trackerInitialized = true;
                this.mPublicKey = publicKey;
                this.mSiteId = siteId;
                this.mSdkVersion = sdkVersion;
                this.mContext = context;
            }
        }
    }

    /**
     * @param publicKey  The public key of the merchant. Cannot be {@code null}.
     * @param siteId     The site that comes in the preference. Cannot be {@code null}.
     * @param sdkVersion The Mercado Pago sdk version. Cannot be {@code null}.
     * @param context    Reference to Android Context. Cannot be {@code null}.
     * @return True if all parameters are valid. False if any parameter is invalid
     */
    private boolean areInitParametersValid(String publicKey, String siteId, String sdkVersion, Context context) {
        return !isEmpty(publicKey) && !isEmpty(sdkVersion) && !isEmpty(siteId) && context != null;
    }

    /**
     * Check if MPTracker is initialized
     *
     * @return True if is initialized. False if is not initialized.
     */
    private boolean isTrackerInitialized() {
        return this.mPublicKey != null && this.mSdkVersion != null && this.mSiteId != null && this.mContext != null;
    }

    /**
     * Get the set site
     *
     * @return The site that is set in the first track. if it is null returns an empty string
     */
    private String getSiteId() {
        return mSiteId == null ? DEFAULT_SITE : mSiteId;
    }

    /**
     * Indicates if a payment was done by card or not
     *
     * @param paymentTypeId The payment type id of the payment tracked
     * @return True if it is a card payment. False if not a card payment.
     */
    private Boolean isCardPaymentType(String paymentTypeId) {
        return paymentTypeId.equals("credit_card") || paymentTypeId.equals("debit_card") || paymentTypeId.equals("prepaid_card");
    }

    private TrackingStrategy setTrackingStrategy(Context context, Event event, String strategy) {
        if (isBatchStrategy(strategy)) {
            trackingStrategy = new BatchTrackingStrategy(database, new ConnectivityCheckerImpl(context), mMPTrackingService);
        } else if (isForcedStrategy(strategy)) {
            trackingStrategy = new ForcedStrategy(database, new ConnectivityCheckerImpl(context), mMPTrackingService);
        } else if (isRealTimeStrategy(strategy)) {
            trackingStrategy = new RealTimeTrackingStrategy(mMPTrackingService);
        } else {
            trackingStrategy = new NoOpStrategy();
        }
        return trackingStrategy;
    }

    private boolean isForcedStrategy(String strategy) {
        return TrackingUtil.FORCED_STRATEGY.equals(strategy);
    }

    private boolean isBatchStrategy(String strategy) {
        return TrackingUtil.BATCH_STRATEGY.equals(strategy);
    }

    private boolean isRealTimeStrategy(String strategy) {
        return TrackingUtil.REALTIME_STRATEGY.equals(strategy);
    }

    private boolean isErrorScreen(String name) {
        return TrackingUtil.SCREEN_NAME_ERROR.equals(name);
    }

    private boolean isResultScreen(String name) {
        return TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_APPROVED.equals(name)
                || TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_PENDING.equals(name)
                || TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_REJECTED.equals(name)
                || TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_INSTRUCTIONS.equals(name);
    }

    public Event getEvent() {
        return mEvent;
    }

    public void clearExpiredTracks() {
        initializeDatabase();
        this.database.clearExpiredTracks();
    }

    public TrackingStrategy getTrackingStrategy() {
        return trackingStrategy;
    }
}