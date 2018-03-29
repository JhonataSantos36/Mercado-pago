package com.mercadopago.preferences;

import com.mercadopago.lite.constants.ProcessingModes;
import com.mercadopago.lite.preferences.ServicePreference;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by acollerone on 8/17/17.
 */

public class ServicePreferenceTest {

    @Test
    public void testSetAggregatorAsProcessingModeAndEnableBankDealsAndEnableEmailConfirmationCell() {
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setAggregatorAsProcessingMode()
                .build();
        assertTrue(servicePreference.getProcessingModeString().equals(ProcessingModes.AGGREGATOR));
        assertTrue(servicePreference.shouldShowBankDeals());
        assertTrue(servicePreference.shouldShowEmailConfirmationCell());
    }

    @Test
    public void testSetGatewayAsProcessingModeAndDisableBankDealsAndDisableEmailConfirmationCell() {
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setGatewayAsProcessingMode()
                .build();
        assertTrue(servicePreference.getProcessingModeString().equals(ProcessingModes.GATEWAY));
        assertFalse(servicePreference.shouldShowBankDeals());
        assertFalse(servicePreference.shouldShowEmailConfirmationCell());
    }

    @Test
    public void testSetHybridAsProcessingModeAndDisableBankDealsAndDisableEmailConfirmationCell() {
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setHybridAsProcessingMode()
                .build();
        assertTrue(servicePreference.getProcessingModeString().equals(ProcessingModes.HYBRID));
        assertFalse(servicePreference.shouldShowBankDeals());
        assertFalse(servicePreference.shouldShowEmailConfirmationCell());
    }
}
