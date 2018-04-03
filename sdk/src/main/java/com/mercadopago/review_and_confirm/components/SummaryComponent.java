package com.mercadopago.review_and_confirm.components;

import android.support.annotation.NonNull;

import com.mercadopago.components.Component;
import com.mercadopago.components.RendererFactory;
import com.mercadopago.review_and_confirm.SummaryProvider;
import com.mercadopago.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.review_and_confirm.models.SummaryModel;

public class SummaryComponent extends Component<SummaryComponent.SummaryProps, Void> {

    public static class SummaryProps {
        final SummaryModel summaryModel;
        final ReviewAndConfirmPreferences reviewAndConfirmPreferences;

        private SummaryProps(final SummaryModel summaryModel,
                             final ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
            this.summaryModel = summaryModel;
            this.reviewAndConfirmPreferences = reviewAndConfirmPreferences;
        }

        public static SummaryProps createFrom(SummaryModel summaryModel, ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
            return new SummaryProps(summaryModel, reviewAndConfirmPreferences);
        }
    }

    private final SummaryProvider provider;

    static {
        RendererFactory.register(SummaryComponent.class, SummaryRenderer.class);
    }

    SummaryComponent(@NonNull final SummaryComponent.SummaryProps props,
                     @NonNull final SummaryProvider provider) {
        super(props);
        this.provider = provider;
    }

    FullSummary getFullSummary() {
        return new FullSummary(props, provider);
    }

    CompactSummary getCompactSummary() {
        return new CompactSummary(props.summaryModel);
    }
}
