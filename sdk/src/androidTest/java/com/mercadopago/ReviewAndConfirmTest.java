package com.mercadopago;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.controllers.CustomReviewablesHandler;
import com.mercadopago.model.Item;
import com.mercadopago.model.Reviewable;
import com.mercadopago.preferences.ReviewScreenPreference;
import com.mercadopago.providers.ReviewAndConfirmProviderImpl;
import com.mercadopago.util.CurrenciesUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReviewAndConfirmTest {

    @Before
    public void cleanUp() {
        CustomReviewablesHandler.getInstance().clear();
    }

    @Test
    public void whenCustomItemReviewableSetThenProvideIt() {
        MockedReviewable customReviewable = new MockedReviewable();

        //Add item review to Handler
        CustomReviewablesHandler.getInstance().setItemsReview(customReviewable);

        //Create preference
        ReviewScreenPreference reviewPreference = new ReviewScreenPreference.Builder()
                .setItemsSummary(customReviewable)
                .build();

        //Create provider
        ReviewAndConfirmProviderImpl reviewScreenProviderImpl =
                new ReviewAndConfirmProviderImpl(InstrumentationRegistry.getContext(), reviewPreference);

        Reviewable providedReviewable = reviewScreenProviderImpl.getItemsReviewable(CurrenciesUtil.CURRENCY_ARGENTINA, new ArrayList<Item>());
        providedReviewable.draw();

        assertTrue(customReviewable.drawn);
    }

    private class MockedReviewable extends Reviewable {

        private boolean drawn = false;

        @Override
        public void draw() {
            drawn = true;
        }

        @Override
        public void initializeControls() {
            //Do something
        }

        @Override
        public View inflateInParent(ViewGroup parent, boolean attachToRoot) {
            return null;
        }

        @Override
        public View getView() {
            return null;
        }
    }
}
