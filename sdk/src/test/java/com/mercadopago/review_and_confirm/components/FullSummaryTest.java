package com.mercadopago.review_and_confirm.components;

import com.mercadopago.review_and_confirm.SummaryProvider;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.review_and_confirm.models.SummaryModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FullSummaryTest {

    @Mock
    SummaryProvider provider;

    @Mock SummaryModel model;

    @Mock ReviewAndConfirmPreferences preferences;

    private FullSummary fullSummary;

    @Before
    public void setUp() {
        fullSummary = new FullSummary(SummaryComponent.SummaryProps.createFrom(model, preferences), provider);
    }

    @Test
    public void noop_test() {
        assert true;
    }
}
