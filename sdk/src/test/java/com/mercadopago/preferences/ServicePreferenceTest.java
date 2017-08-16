package com.mercadopago.preferences;

import com.mercadopago.constants.ProcessingModes;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by acollerone on 8/17/17.
 */

public class ServicePreferenceTest {

    @Test
    public void testSetAggregatorAsProcessingModeAndEnableBankDeals() {
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setAggregatorAsProcessingMode()
                .build();
        assertTrue(servicePreference.getProcessingModeString().equals(ProcessingModes.AGGREGATOR));
        assertTrue(servicePreference.showBankDealsByProcessingMode());
    }

    @Test
    public void testSetGatewayAsProcessingModeAndDisableBankDeals() {
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setGatewayAsProcessingMode()
                .build();
        assertTrue(servicePreference.getProcessingModeString().equals(ProcessingModes.GATEWAY));
        assertFalse(servicePreference.showBankDealsByProcessingMode());
    }

    @Test
    public void testSetHybridAsProcessingModeAndDisableBankDeals() {
        ServicePreference servicePreference = new ServicePreference.Builder()
                .setHybridAsProcessingMode()
                .build();
        assertTrue(servicePreference.getProcessingModeString().equals(ProcessingModes.HYBRID));
        assertFalse(servicePreference.showBankDealsByProcessingMode());
    }
}
